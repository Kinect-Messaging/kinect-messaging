#!/bin/sh

gpg --batch --yes --passphrase="$GPG_SECRET_PASSPHRASE" --symmetric --cipher-algo AES256 ./deploy/modules/"${ENV_NAME}".parameters.json