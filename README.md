### This server-side mod adds a simple command to redeem and store xp in a bottle for later use!
**Command:** `/redeem <amount> xp | levels`

For e.g. `/redeem 100 xp` stores 100 xp points, whereas `/redeem 10 levels` stores the xp for 10 levels in a Bottle o' Enchanting.

If you run the command while holding a bottle that is not full, that bottle will be filled first and the amount would be capped by the space in that bottle. Running the command again would give you a new bottle.

To redeem the maximum amount possible run `/redeem max`. (Would depend on player's current xp **or** the maximum space of a bottle)

_Keep in mind, if u redeem 10 levels while being at 30, u will get an xp amount that is different from when u redeem 10 levels while being at 20, for example._

---
To get the xp back again, simply right click to slowly drain the bottle just like normal. 

The xp which is given out per click and the maximum amount a bottle can hold are configurable: `xp_rate : 10` and `max_xp : 1395`.

If u sneak while right-clicking u can get all the xp at once.

The stored xp can be monitored by the durability bar or the description.

---
On death, the player drops a bottle containing a fraction of their xp. This can be configured under `xp_percentage_on_death : 50`.

---
Run `/xpinfo totalxp` to get your total xp points.

Run `/xpinfo xplevels <levels>` to get the xp points required to reach that level.

---
For the config file to appear load and run the mod once, then u can change the values in `/config/redeemxp.json5`

---
If you are on singleplayer you can use Mod Menu to configure the values.

### TL;DR

`/redeem <amount> xp | levels`

`/redeem max`

`/xpinfo totalxp`

`/xpinfo xplevels <levels>`

`xp_rate : 10`

`max_xp : 1395`

`xp_percentage_on_death : 50`