#!/usr/bin/env bash

#----------------------------------------------------------------------------#
# This file is part of JBlake2.                                              #
# Copyright (C) 2017-2018 Osman Koçak <kocakosm@gmail.com>                   #
#                                                                            #
# This program is free software: you can redistribute it and/or modify it    #
# under the terms of the GNU Lesser General Public License as published by   #
# the Free Software Foundation, either version 3 of the License, or (at your #
# option) any later version.                                                 #
# This program is distributed in the hope that it will be useful, but        #
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY #
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public     #
# License for more details.                                                  #
# You should have received a copy of the GNU Lesser General Public License   #
# along with this program. If not, see <http://www.gnu.org/licenses/>.       #
#----------------------------------------------------------------------------#

set -eEuo pipefail
IFS=$'\n\t'

readonly ARCH='x86_64'
readonly MIN_BASH_VERSION='4.1'
readonly MIN_DOCKER_VERSION='17.05'
readonly BASE_IMAGE_NAME='amd64/debian:stretch'
readonly BUILD_IMAGE_NAME='jblake2-build'
readonly ENTRYPOINT_SCRIPT_NAME='entrypoint.sh'

check_bash_version()
{
	if [[ "${BASH_VERSION}" < "${MIN_BASH_VERSION}" ]]; then
		printf 'Error: you need GNU Bash version %s+ to run this script\n' "${MIN_BASH_VERSION}" >&2
		exit 1
	fi
}

check_cpu_architecture()
{
	if ! [[ "$(uname -m)" = "${ARCH}" ]]; then
		printf 'Error: you need an %s CPU to run this script\n' "${ARCH}" >&2
		exit 1
	fi
}

check_docker_version()
{
	if ! [[ -x $(command -v docker) ]]; then
		printf 'Error: you need Docker to run this script\n' >&2
		exit 1
	fi
	if [[ "$(docker version --format '{{.Server.Version}}')" < "${MIN_DOCKER_VERSION}" ]]; then
		printf 'Error: you need Docker version %s+ to run this script\n' "${MIN_DOCKER_VERSION}" >&2
		exit 1
	fi
}

write_entrypoint()
{
	cat <<- 'EOF' > "${ENTRYPOINT_SCRIPT_NAME}"
	#!/usr/bin/env bash
	set -eEuo pipefail
	IFS=$'\n\t'

	readonly MIN_JBLAKE2_VERSION='0.4'
	readonly MAVEN_CENTRAL_URL='https://repo1.maven.org/maven2'

	read_version()
	{
		# disable shellcheck warning on '${project.version}' since it is not a shell variable
		# shellcheck disable=SC2016
		mvn --quiet -Dexec.executable='printf' -Dexec.args='${project.version}' exec:exec
	}

	check_version()
	{
		local -r jblake2_version=${1}
		if [[ "${jblake2_version}" = *-SNAPSHOT ]]; then
			printf 'Error: SNAPSHOT version\n' >&2
			exit 1
		fi
		if [[ "${jblake2_version}" < "${MIN_JBLAKE2_VERSION}" ]]; then
			printf 'Error: JBlake2 is reproducible since version %s\n' "${MIN_JBLAKE2_VERSION}" >&2
			exit 1
		fi
	}

	sha256sum()
	{
		local -r file=${1}
		command sha256sum "${file}" | cut -d ' ' -f 1
	}

	download_jar_from_central()
	{
		local -r jblake2_version=${1}
		curl --silent --location --remote-name \
			"${MAVEN_CENTRAL_URL}/org/kocakosm/jblake2/${jblake2_version}/jblake2-${jblake2_version}.jar"
		printf 'jblake2-%s.jar' "${jblake2_version}"
	}

	build_jar_from_source()
	{
		mvn --quiet clean package -Dmaven.test.skip=true
		ls target/jblake2-*.jar
	}

	main()
	{
		printf 'Reading version...\t\t'
		local -r version=$(read_version)
		printf '%s\n' "${version}"
		check_version "${version}"
		printf 'Downloading jar from central...\t'
		local -r downloaded_jar=$(download_jar_from_central "${version}")
		local -r downloaded_jar_hash=$(sha256sum "${downloaded_jar}")
		printf 'sha256:%s\n' "${downloaded_jar_hash}"
		printf 'Building jar from source...\t'
		local -r built_jar=$(build_jar_from_source)
		local -r built_jar_hash=$(sha256sum "${built_jar}")
		printf 'sha256:%s\n' "${built_jar_hash}"
		if ! [[ "${built_jar_hash}" = "${downloaded_jar_hash}" ]]; then
			printf 'Hashes mismatch: JBlake2 %s could not be verified\n' "${version}"
			exit 1
		fi
		printf 'Hashes match: successfully verified JBlake2 %s\n' "${version}"
	}

	main
	EOF
	chmod +x "${ENTRYPOINT_SCRIPT_NAME}"
}

build_docker_image()
{
	# We're using snapshot.debian.org repositories so that we can install the exact versions used to release the
	# project. See https://snapshot.debian.org for more information.
	# Though not required (since we rely on snapshot.debian.org), versions of key packages are explicitly specified
	# for documentation purposes. Note: on Debian systems, you can use 'apt list -a <package>' to list all
	# available versions of a particular package.
	docker build --quiet --tag "${BUILD_IMAGE_NAME}" . -f -<<- EOF
	FROM "${BASE_IMAGE_NAME}"
	RUN printf 'deb http://snapshot.debian.org/archive/debian/20181220T210254Z/ stretch main' > /etc/apt/sources.list \
		&& apt update && apt install -y --no-install-recommends \
		curl \
		maven=3.3.9-4 \
		openjdk-8-jdk-headless=8u181-b13-2~deb9u1 \
		openjdk-8-jre-headless=8u181-b13-2~deb9u1 \
		&& rm -rf /var/lib/apt/lists/*
	COPY . /usr/src/jblake2
	WORKDIR /usr/src/jblake2
	COPY ./"${ENTRYPOINT_SCRIPT_NAME}" /
	ENTRYPOINT ["/${ENTRYPOINT_SCRIPT_NAME}"]
	EOF
}

run_build_container()
{
	docker run --rm "${BUILD_IMAGE_NAME}"
}

cleanup()
{
	local -r result=${?}
	docker image rm "${BUILD_IMAGE_NAME}" "${BASE_IMAGE_NAME}" > /dev/null 2>&1 || true
	rm "${ENTRYPOINT_SCRIPT_NAME}" > /dev/null 2>&1 || true
	exit ${result}
}

main()
{
	trap cleanup EXIT ERR
	printf 'Checking prerequisites...\t'
	check_bash_version
	check_cpu_architecture
	check_docker_version
	printf 'OK\n'
	printf 'Building docker image...\t'
	write_entrypoint
	build_docker_image
	printf 'Running build container...\n'
	run_build_container
}

main
