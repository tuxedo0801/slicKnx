# slicKnx
Simplified KNX API, based on Calimero 2.2.1-SNAPSHOT

Sample:



Knx knx = new Knx("1.1.254");
        
knx.addGroupAddressListener("1/1/15", new GroupAddressListener() {

    @Override
    public void readRequest(GroupAddressEvent event) {
    }

    @Override
    public void readResponse(GroupAddressEvent event) {
    }

    @Override
    public void write(GroupAddressEvent event) {
        try {
            System.out.println("Received update for 1/1/15: "+event.asBool());
        } catch (KnxFormatException ex) {
            ex.printStackTrace();
        }
    }
});

knx.writeBoolean("1/1/15", true);
