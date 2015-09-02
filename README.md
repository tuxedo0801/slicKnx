# slicKnx
Simplified KNX API, based on Calimero 2.2.1-beta

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
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    ...
    <repository>
        <id>root1-releases</id>
        <name>root1.de Releases Repository</name>
        <layout>default</layout>
        <url>http://nexus.root1.de/content/repositories/releases/</url>
        <snapshots>
            <enabled>false</enabled>
        <snapshots>
    </repository>
    <repository>
        <id>root1-snapshots</id>
        <name>root1.de Snapshots Repository</name>
        <layout>default</layout>
        <url>http://nexus.root1.de/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        <snapshots>
    </repository>
</repositories>
```
