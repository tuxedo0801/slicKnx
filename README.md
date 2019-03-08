# slicKnx
Simplified KNX API, based on Calimero 2.4-RC1

Sample:

```java
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
```

--------------------------------

Maven POM configuration:

```xml
<dependencies>
    ...
    <dependency>
        <groupId>de.root1</groupId>
        <artifactId>slicknx</artifactId>
        <version>2.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    ...
    <repositories>
        <repository>
            <id>root1-releases</id>
            <url>http://maven.root1.de/repository/releases/</url>
        </repository>
        <repository>
            <id>root1-snapshots</id>
            <url>http://maven.root1.de/repository/snapshots/</url>
        </repository>
    </repositories>
</repositories>
```
