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
4. In your Chatster `config` folder, create a file called `vaultfile.json` and add the following properties (adjust them to your requirements)
```
{
   "executablePath": "Optional file system path to your Vaultfile executable",
   "vaultfileName": "Optional custom name for your vault file. If empty, 'credentials.vault' will be applied",
   "keyFile": {
      "path": "Optional custom path for your private/public key pair if different from $HOME/.vaultfile/$USER.key",
      "keyName": "Optional custom name for your key in the vaultfile if different from $USER"
   }
}
```