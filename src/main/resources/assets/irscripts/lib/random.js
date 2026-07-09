/**
 * Python {@code random} module subset.
 */
(function (global) {
    var seedState = null;

    function nextSeed() {
        if (seedState === null) {
            return Math.random();
        }
        seedState = (seedState * 9301 + 49297) % 233280;
        return seedState / 233280.0;
    }

    function toInt(value) {
        return value >= 0 ? Math.floor(value) : Math.ceil(value);
    }

    global.random = {
        random: function () {
            return nextSeed();
        },

        seed: function (value) {
            if (value === undefined || value === null) {
                seedState = null;
                return;
            }
            seedState = Math.abs(toInt(Number(value))) % 233280;
            if (seedState === 0) {
                seedState = 1;
            }
        },

        uniform: function (a, b) {
            a = Number(a);
            b = Number(b);
            if (isNaN(a)) a = 0;
            if (isNaN(b)) b = 1;
            if (a > b) {
                var tmp = a;
                a = b;
                b = tmp;
            }
            return a + (b - a) * nextSeed();
        },

        randint: function (a, b) {
            a = toInt(Number(a));
            b = toInt(Number(b));
            if (a > b) {
                var tmp = a;
                a = b;
                b = tmp;
            }
            return a + toInt(nextSeed() * (b - a + 1));
        },

        choice: function (seq) {
            if (!seq || seq.length === undefined || seq.length === 0) {
                throw new Error("random.choice: empty sequence");
            }
            var index = toInt(nextSeed() * seq.length);
            if (index >= seq.length) {
                index = seq.length - 1;
            }
            return seq[index];
        },

        shuffle: function (seq) {
            if (!seq || seq.length === undefined || seq.length < 2) {
                return seq;
            }
            for (var i = seq.length - 1; i > 0; i--) {
                var j = toInt(nextSeed() * (i + 1));
                var tmp = seq[i];
                seq[i] = seq[j];
                seq[j] = tmp;
            }
            return seq;
        }
    };
})(this);
