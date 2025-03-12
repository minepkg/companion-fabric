# minepkg companion mod

This fabric mod connects the minepkg CLI to Minecraft.

It currently enables minepkg to skip the main menu and immediately join
a server or start a single player session.


## Developer Notes
To build and test this mod, use the following workflow:

1. Build the mod and launch Minecraft using: `minepkg launch --minepkgCompanion none`.  Optionally, specify a Minecraft version using `-m <minecraft-version>`. 
2. Create a world; call it, for example, `test_world`.
3. Set the environment variable: `export MINEPKG_COMPANION_PLAY=local://test_world`
4. Launch Minecraft again. The mod should now load the world on startup.

To test joining a server locally:

1. Set the environment variable: `export MINEPKG_COMPANION_PLAY=server://localhost`
2. Start a server using: `minepkg launch -m --minepkgCompanion none --server -a`
3. In a separate terminal, join the server with the client using: `minepkg launch -m --minepkgCompanion none`

### Troubleshooting
- Try building with `./gradlew build clean`
- See if there are newer compatible versions of dependencies (Fabric Loom, Fabric Loader, Gradle)
- Make sure you have the correct java version. (`java --version`) 
- launch with `--debug` to get spammed with logs

## License

This mod is MIT licenced.
