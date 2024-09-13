#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command
# --yes to assume "yes" for questions
echo "$APP_NAME"
echo "$GPG_SECRET_PASSPHRASE"
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_SECRET_PASSPHRASE" \
--output ./deploy/main.parameters.json ./deploy/modules/"${APP_NAME}".parameters.json.gpg
