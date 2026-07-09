/**
 * Python {@code time} module subset for IR Scripts.
 * Native hooks: {@code __native} (world/stock ticks, non-blocking sleep).
 */
(function (global) {
    var native = global.__native;
    var MS_PER_TICK = 50.0;
    var TICKS_PER_SECOND = 20.0;

    function toNumber(value, fallback) {
        if (value === undefined || value === null) {
            return fallback;
        }
        var n = Number(value);
        return isNaN(n) ? fallback : n;
    }

    function structTimeFromDate(date, isDst) {
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        var hour = date.getHours();
        var minute = date.getMinutes();
        var second = date.getSeconds();
        var weekday = date.getDay();
        var yday = dayOfYear(year, month, day);
        var dst = isDst === undefined ? -1 : (isDst ? 1 : 0);

        var st = [second, minute, hour, day, month, year, weekday, yday, dst];
        st.tm_sec = second;
        st.tm_min = minute;
        st.tm_hour = hour;
        st.tm_mday = day;
        st.tm_mon = month;
        st.tm_year = year;
        st.tm_wday = weekday;
        st.tm_yday = yday;
        st.tm_isdst = dst;
        return st;
    }

    function dayOfYear(year, month, day) {
        var days = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334];
        var yday = days[month - 1] + day;
        if (month > 2 && isLeapYear(year)) {
            yday += 1;
        }
        return yday - 1;
    }

    function isLeapYear(year) {
        return (year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0);
    }

    function pad2(n) {
        return n < 10 ? "0" + n : String(n);
    }

    function strftime(format, t) {
        if (format === undefined || format === null) {
            format = "";
        }
        var st = Array.isArray(t) || (t && t.tm_year !== undefined) ? t : localtime(t);
        var mapping = {
            "%Y": String(st.tm_year),
            "%y": pad2(st.tm_year % 100),
            "%m": pad2(st.tm_mon),
            "%d": pad2(st.tm_mday),
            "%H": pad2(st.tm_hour),
            "%M": pad2(st.tm_min),
            "%S": pad2(st.tm_sec),
            "%A": ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"][st.tm_wday],
            "%a": ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"][st.tm_wday],
            "%B": ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"][st.tm_mon],
            "%b": ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"][st.tm_mon],
            "%w": String(st.tm_wday),
            "%j": pad2(st.tm_yday + 1),
            "%%": "%"
        };

        var result = String(format);
        var tokens = ["%Y", "%y", "%m", "%d", "%H", "%M", "%S", "%A", "%a", "%B", "%b", "%w", "%j", "%%"];
        for (var i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            result = result.split(token).join(mapping[token]);
        }
        return result;
    }

    function ctime(seconds) {
        return asctime(localtime(seconds));
    }

    function asctime(st) {
        if (!st || (st.tm_year === undefined && !Array.isArray(st))) {
            st = localtime();
        }
        return strftime("%a %b %d %H:%M:%S %Y", st);
    }

    function mktime(st) {
        if (!st) {
            return time();
        }
        var year = st.tm_year !== undefined ? st.tm_year : st[5];
        var month = (st.tm_mon !== undefined ? st.tm_mon : st[4]) - 1;
        var day = st.tm_mday !== undefined ? st.tm_mday : st[3];
        var hour = st.tm_hour !== undefined ? st.tm_hour : st[2];
        var minute = st.tm_min !== undefined ? st.tm_min : st[1];
        var second = st.tm_sec !== undefined ? st.tm_sec : st[0];
        return new Date(year, month, day, hour, minute, second).getTime() / 1000.0;
    }

    function localtime(seconds) {
        var secs = toNumber(seconds, time());
        return structTimeFromDate(new Date(secs * 1000.0));
    }

    function gmtime(seconds) {
        var secs = toNumber(seconds, time());
        var d = new Date(secs * 1000.0);
        return structTimeFromDate(new Date(Date.UTC(
            d.getUTCFullYear(),
            d.getUTCMonth(),
            d.getUTCDate(),
            d.getUTCHours(),
            d.getUTCMinutes(),
            d.getUTCSeconds()
        )), 0);
    }

    function time(arg) {
        if (arg !== undefined) {
            throw new Error("time.time: setting time is not supported");
        }
        return native.time();
    }

    global.time = {
        time: time,
        monotonic: function () { return native.monotonic(); },
        perf_counter: function () { return native.perfCounter(); },
        process_time: function () { return native.processTime(); },
        sleep: function (seconds) { native.sleep(toNumber(seconds, 0)); },
        ctime: ctime,
        asctime: asctime,
        localtime: localtime,
        gmtime: gmtime,
        mktime: mktime,
        strftime: strftime,
        struct_time: function (tuple) { return localtime(mktime(tuple)); },
        world_tick: function () { return native.worldTick(); },
        stock_tick: function () { return native.stockTick(); },
        ticks_to_seconds: function (ticks) { return native.ticksToSeconds(toNumber(ticks, 0)); },
        seconds_to_ticks: function (seconds) { return native.secondsToTicks(toNumber(seconds, 0)); },
        TICKS_PER_SECOND: TICKS_PER_SECOND,
        SECONDS_PER_TICK: MS_PER_TICK / 1000.0
    };
})(this);
