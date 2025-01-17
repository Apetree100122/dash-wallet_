Recovering Dash
===================

## PROLOGUE

This document describes how you can use a backup file on a standard PC to recover your Dash.
Normally, this shouldn't be needed. It is much preferred to just use **Options > Safety > Restore wallet** from within the Dash Wallet app if you can. This guide is only meant for rare cases:

- Your Android device is destroyed or missing and you do not want or cannot get a new Android
  device.
- Legislation in your country forbids you to continue using the app and you missed the chance to
  move your coins out while it was still legal.
- The app suddenly goes out of service for whatever reason. This event is extremely unlikely,
  given the fact that the app is open source and many developers from all over the world have and
  know the code.

Be aware some of the steps in this tutorial require handling your private keys in the unencrypted
form. Do not expose them to anyone. Whoever knows your private keys can spend your coins on these
keys. It'd good practice that after you are finished handling these keys, they should be
considered compromised, even if they are not. Make sure your system is free of any malware.

We recommend using Ubuntu Linux. You can boot from a Live CD if you want, but if you do please
refrain from sending your coins to a temporary wallet created in that environment, which would be
lost e.g. on a power outage or computer failure. Your desired destination wallet should already be
set up and you should have one of its receiving addresses or a QR code at hand.

Alternatively, you can also use Ubuntu on Windows 10 64-bit, if you've fully upgraded to the Fall Creators Update (version 1709 or later). Open the Windows Start Menu, search for and start `Turn Windows features on or off`. Scroll down and tick the `Windows Subsystem for Linux` feature. Restart your computer when prompted. Next, install `Ubuntu` from the Windows Store. Once the download has completed, select `Launch`. It will prompt you to pick a username and complete the installation. From now on, you can start into a Linux shell by selecting `Ubuntu` from the Windows Start Menu.

You should be at least a bit familiar with the Linux shell. Commands `in fixed-width font like this`
are meant to be executed as a shell command. Before you execute each command by pressing return,
make sure to understand what it does. You will need to adjust some file or directory names.
Commands starting with `sudo apt` will ask for your permission to install software by
requiring your Ubuntu user password.


## PREPARATION

On your PC, within your Linux shell, install the following Ubuntu packages:

    sudo apt install openjdk-8-jdk openjfx android-tools-adb openssl git maven

On your Android device, go to Settings > Developer options and enable "USB debugging". On most
recent devices you need to go to Settings > About first and tap on "Build number" multiple times
until you see the "You are now a developer" message.


## LOCATING THE BACKUP FILES

If you followed the apps guidance your backup files will be located both on-device and off-device.
Let's look at off-device first. When backing up, the app instructed you to archive your backup to
mail or cloud storage. Depending on how you decided, your backup probably ended up as attachment
on an email sent to yourself (look into your Inbox and Sent folders) or uploaded to a Google Drive
or Dropbox kind of service. Just save the backup file to your PCs filesystem. Skip the rest of this
paragraph directly to DECRYPTING.

You cannot find your backup? If you're still using the device you made the backup with, there is
a good chance the backup is on-device. Use:

	adb shell ls -l /sdcard/Download/dash-wallet-*

It will list any backup files present. Pick one and use:

	adb pull /sdcard/Download/dash-wallet-backup-2014-11-01

to copy the file to your PC.

Note:  Older versions of this wallet saved the files as darkcoin-wallet-*, etc.


## DECRYPTING

You now have your backup file on your PC. Wallet backups are encrypted. Let's decrypt it using:

    openssl enc -d -aes-256-cbc -md md5 -a -in dash-wallet-backup-2014-11-01 > dash-wallet-decrypted-backup

It will ask you for a decryption password, which is your backup password. If it prints
"bad password" you've got the wrong password, but if it doesn't print anything your password might
still be wrong. We can only be sure by looking at the decrypted data.

Historically there is two backup formats. Let's look at the first printable characters in the file:

	cat dash-wallet-decrypted-backup | tr -cd "[:print:]" | awk '{print $1}'

If it prints "org.darkcoin.production", you got the right password and the backup file uses the
darkcoinj protobuf format. This backup format was introduced in v3.47 (May 2014). Skip to
RECOVERING FROM PROTOBUF WALLET FORMAT.

If it prints just a hash sign (`#`), you got the right password and the backup file uses the old
text based private key format. Skip to RECOVERING FROM BASE58 KEY FORMAT.

If it prints something else or nothing, you likely didn't get the password right. Passwords are
case sensitive, and make sure you didn't accidentally type a space character in front or after the
password.


## RECOVERING FROM PROTOBUF WALLET FORMAT

We need wallet-tool from dashj. First, in a working directory, let's get dashj:

	git clone -b release-19 https://github.com/HashEngineering/dashj.git

Make sure everything is compiled and ready to go by using once:

	cd dashj/tools
	./wallet-tool

Now use wallet-tool to sync the wallet from your backup:

	./wallet-tool reset --wallet=/tmp/dash-wallet-decrypted-backup
	./wallet-tool sync --wallet=/tmp/dash-wallet-decrypted-backup --debuglog

The sync process will take anywhere from a few minutes to hours. Wallet-tool will return to the
shell prompt if its finished syncing. Have a look at the wallet:

	./wallet-tool dump --wallet=/tmp/dash-wallet-decrypted-backup

Does the balance look right? You can see all transactions that ever touched your wallet. Now empty
your entire wallet to the desired destination wallet:

	./wallet-tool send --wallet=/tmp/dash-wallet-decrypted-backup --output=<receiving address of destination wallet>:ALL

If your wallet was protected by a spending PIN, you need to supply that PIN using the
`--password=<PIN>` option. Be extra careful with this command to get all parameters right. If it
succeeds, it will print the transaction hash of the created transaction. You can use that on
a block explorer to watch, or just open the destination wallet and watch from there. If your coins
are confirmed, you're done and you can skip the next paragraph to EPILOGUE.

You can also get a list of the private keys. If your wallet has a spending PIN set you need to decrypt it first, otherwise the private keys won't appear. Note that when you decrypt the wallet *the private keys can be accessed (and your Dash stolen) by anyone with access to the system*, including malware or other users. Unless you fully trust the security of the computer consider running it on an offline system with no network connectivity.

    ./wallet-tool decrypt --wallet=/tmp/dash-wallet-decrypted-backup --password=<PIN>

Then to get the private keys use:

    ./wallet-tool dump --wallet=/tmp/dash-wallet-decrypted-backup --dump-privkeys

Look for `priv WIF=<...>`, where `<...>` will be your private keys in wallet import format. Be careful where you put them, as anybody getting access to them will be able to steal your coins. Consider securely deleting the decrypted wallet once you get your private keys.

## RECOVERING FROM BASE58 KEY FORMAT

Have a deeper look at the backup file (these files were produced by Darkcoin Wallet):

	cat darkcoin-wallet-decrypted-backup

You'll see each line contains a key in WIF (wallet import format), technically Base58. The
datetime string after each key is the birthdate of that key which you can ignore for the purpose
of this one-time recovery.

Another option is importing each individual key into one of [Electrum Dash] (https://electrum.dash.org/#download)
or [Dash Core] (https://www.dash.org/downloads/).

As soon as you see your whole balance again, empty your entire wallet to the desired destination
wallet. Please do not continue to use the imported wallet. Remember you just operated on
unencrypted keys which can be dangerous, so it's good practice to handle them as if they were
compromised even if they in fact aren't.


## EPILOGUE

Let us know if this document helped you with recovering your coins!