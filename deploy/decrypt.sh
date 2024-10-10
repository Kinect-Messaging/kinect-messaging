#!/bin/sh

# Decrypt the file
# --batch to prevent interactive command
# --yes to assume "yes" for questions
echo "$ENV_NAME"
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_SECRET_PASSPHRASE" \
--output ./deploy/main.parameters.json ./deploy/modules/"${ENV_NAME}".parameters.json.gpg
