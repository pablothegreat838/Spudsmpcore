# SpudSMP

Spigot/Paper 1.12.2 survival core plugin targeting Java 8.

## Build

```bash
mvn -DskipTests package
```

The plugin is generated at `target/SpudSMP.jar`. Copy it to a 1.12.2 server's `plugins` directory. Vault and PlaceholderAPI are optional soft dependencies.

## Main commands

`/help`, `/spawn`, `/pvp`, `/tpa`, `/tpaccept`, `/tpdeny`, `/tpr`, `/shop`, `/ah`, `/leaderboards`, `/staff`, `/ptp`, `/spudclear`, `/portal`, `/shutdown`.

Administrative commands require `spudsmp.admin`; staff commands require operator status or `spudsmp.admin`.
