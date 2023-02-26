# Implementation for minecraft servers
> This branch contains the `rocket` protocol server implementation for the `Minecraft` game.

## Installation
1. Download `.jar` file from `releases`
2. Put that file in your server plugins directory.
3. Run the server and configure.

## Configuration
Configuration of this protocol is very simple, in the plugins directory you will see a directory `Rocket`, generate a key using the command:

```sh
python -c "\n\nimport base64, os\nprint(base64.urlsafe_b64encode(os.urandom(32)).decode('utf-8'))"
```

Then open the `config.yml` file and paste the generated key into the `secret_key` section.
Below is a sample configuration:

> **_NOTE:_** Remember not to use my key from the example, it should be generated individually

```yml
secret_key: rUc_HJItDCwdkZ0esm8uKUh4yIc8GsYSTNFQleUcmI0=
port: 5543
thread_pool_size: 5
```

## Tested on:
> **_NOTE:_**  I have not tested all the versions and branches of the `spigot`, but I suspect that it should work on almost every version of the game, but if you encounter any problems with the operation, please create an issue with all the necessary information.

- Paper-1.19.3