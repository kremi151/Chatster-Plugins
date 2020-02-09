# Vaultfile plugin for Chatster

## Intro

This plugin uses the `vaultfile` shared secret manager written by Daniel Gray (https://github.com/danielthegray/vaultfile).
<br/>It exposes a CredentialStore to Chatster which allows to read and store credentials from/to a vault file.

## Setup

1. Get the binary for `vaultfile` and place it in either a directory accessible by your `PATH` configuration or in some custom location
<br/>You can get the binary from the [Github page](https://github.com/danielthegray/vaultfile) of `vaultfile` or by compiling it yourself
2. If you haven't already, generate a new private/public keypair for `vaultfile` and create a new vaultfile in the same directory as Chatster lives
<br/>See https://github.com/danielthegray/vaultfile#workflow
3. Add your secrets to the newly created vaultfile
<br/>See https://github.com/danielthegray/vaultfile#adding-a-secret-password-api-key-etc
4. In your Chatster folder, create a file called `vaultfile.properties` and add the following properties (adjust them to your requirements)
```
# (Optional) The name of the vaultfile. Unless specified otherwise, it will be called credentials.vault
vaultfile.name=custom-name.vault

# (Optional) If the vaultfile binary is not accessible by your PATH configuration, specify its exact location here
vaultfile.executable.path=/home/user/custom-path/vaultfile

# (Optional) If you want to use a different path for your private/public key pair than the default one created in $HOME/.vaultfile/$USER.key, specify the exact location here
vaultfile.keyfile.path=path/to/private_key_file

# (Optional) If your key is registered with a name different to the one of the current user name, specify it here
vaultfile.keyfile.keyname=myKeyName
```