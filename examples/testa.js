function test1() {
    print("LOOP speedRatio=" + stock.readout.getSpeed()
        + " speedKmh=" + stock.getSpeedKmh()
        + " temp=" + stock.readout.getTemperature());
}

function test2() {
    print("ONCE uuid=" + stock.getUuid());
}

function test3() {
    stock.sound.play("sounds/horn_1.ogg", 0.8);
    print("BUTTON played horn at tick " + stock.getTickCount());
}
