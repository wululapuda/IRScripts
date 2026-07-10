[ZH](Script-Modes-ZH) | **EN**

[< Contents](Handbook-EN)

---

# Execution modes

| JSON value | When called |
|------------|-------------|
| `LOOP-TICK` | Every game tick (20/sec) |
| `LOOP-SCRIPTS` | After previous run completes |
| `ONCE` | Once on entity creation |
| `BUTTON` | Player clicks GUI button |

Legacy `"LOOP"` = `LOOP-TICK`.

## LOOP-TICK

Run sounds, monitoring. Keep lightweight — see [Examples](Examples-EN).

## LOOP-SCRIPTS

Sequential logic with `utilPlay` / `time.sleep`. Uses Rhino continuations.

## Errors

LOOP modes: uncaught error → function permanently disabled for that instance.
