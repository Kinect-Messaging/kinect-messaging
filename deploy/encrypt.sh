#!/bin/sh
#sed -i '' "s/#APP_NAME#/${APP_NAME}/g" deploy/modules/dev.parameters.json
gpg --batch --yes --passphrase="$GPG_SECRET_PASSPHRASE" --symmetric --cipher-algo AES256 ./deploy/modules/"${ENV_NAME}".parameters.json