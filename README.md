# Minecraft Event Show Plugin

Spectacle Events is a Minecraft plugin for Paper that creates a long, animated event show at a player's location with particle effects, item displays, fireworks, titles, sounds, weather changes, and multiple themed event phases.

## Install

1. Use Java 21 or newer.
2. Run a Paper 1.21.11 server.
3. Build the plugin with Maven:
   `mvn clean package`
4. Copy the generated `target/plugin.jar` file into the server's `plugins` folder.
5. Start or restart the server.
6. Make sure the user has the `eventshow.use` permission. This permission is granted to server operators by default.

## Use

1. Stand at the location where the show should begin.
2. Run `/event start`.
3. Watch the event sequence, which lasts approximately 75 seconds.
4. Use `/event stop` to stop the active event.
5. Only one event can run at a time.

The show includes an opening particle display, potion effects, falling apples and enchanted books, hearts and cherry particles, snow and water effects, an enchanted forest, stardust, a dark weather interlude, and a firework finale. The event is centered on the location of the player who starts it.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/64f7dbc1-3700-490b-b581-79d9ad4f5de1?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=64f7dbc1-3700-490b-b581-79d9ad4f5de1
