### This server-side mod adds a simple command to redeem and store xp in a bottle for later use!
**Command:** `/redeemxp <amount> xp | levels`

For e.g. `/redeemxp 100 xp` stores 100 xp points, whereas `/redeemxp 10 levels` stores the xp for 10 levels in a Bottle o' Enchanting.

If you run the command while holding a bottle that is not full, that bottle will be filled first and the amount would be capped by the space in that bottle. Running the command again would give you a new bottle.

To redeem the maximum amount possible run `/redeemxp max`. (Would depend on player's current xp **or** the maximum space of a bottle)

_Keep in mind, if u redeem 10 levels while being at 30, u will get an xp amount that is different from when u redeem 10 levels while being at 20, for example._

---
To get the xp back again, simply right click to slowly drain the bottle just like normal. 

The xp which is given out per click and the maximum amount a bottle can hold are configurable: `xp_rate : 10` and `max_xp : 1395`.

If u sneak while right-clicking u can get all the xp at once.

The stored xp can be monitored by the durability bar or the description.

---
Run `/redeemxp quickmend` to toggle quick mend feature. When its enabled, open inventory and hover over an item with mending and press offhand key to mend that item using the players xp.

Amount in percentage of each mend can be configured. For balance, a penalty multiplier  can also be configured to increase the use of xp. For example `"quickmend_penalty": 2` means double the xp will be used.

---
On death, the player drops a bottle containing a fraction of their xp. This can be configured under `xp_percentage_on_death : 50`.

---
Run `/redeemxp info totalxp` to get your total xp points.

Run `/redeemxp info xplevels <levels>` to get the xp points required to reach that level.

Run `/redeemxp toggle-repair` to toggle between whether xp orbs repair items with mending **or** increase the player's xp. (This feature can be disabled in the config)

---
For the config file to appear load and run the mod once, then u can change the values in `/config/redeemxp.json5`

---
If you are on singleplayer you can use Mod Menu to configure the values.

### TL;DR

`/redeemxp <amount> xp | levels`

`/redeemxp max`

`/redeemxp info totalxp`

`/redeemxp info xplevels <levels>`

`/redeemxp quickmend`

`/redeemxp toggle-repair`

---
`"max_xp" : 1395`

`"xp_rate" : 10`

`"quick_mend_enabled": true`

`"quickmend_percentage": 10`

`"quickmend_penalty": 1.5`

`"xp_percentage_on_death" : 50`

`"toggle_repair_enabled": true`