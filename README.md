### This server-side mod adds a simple command to redeem and store xp in a bottle for later use!
**Command:** `/redeem <amount> xp | levels`

For e.g. `/redeem 100 xp` stores 100 xp points, whereas `/redeem 10 levels` stores the xp for 10 levels.

To redeem the maximum amount possible run `/redeem max`.

_Keep in mind, if u redeem 10 levels while being at 30, u will get an xp amount that is different from when u redeem 10 levels while being at 20, for example._

---
To get the xp back again, simply right click to slowly drain the bottle just like normal. 

The xp which is given out per click and the maximum amount a bottle can hold are configurable: `xp_rate : 10` and `max_xp : 1395`.

If u sneak while right-clicking u can get all the xp at once.

The stored xp can be monitored by the durability bar or the description.

---
On death, the player drops a bottle containing a fraction of their xp. This can be configured under `xp_percentage_on_death : 50`.

---
Run `/xpstats` to get your total xp points.

---
For the config file to appear load and run the mod once, then u can change the values in `/config/redeemxp.json5`

---
If you are on singleplayer you can use Mod Menu to configure the values.