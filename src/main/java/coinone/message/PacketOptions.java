package coinone.message;

public enum PacketOptions {

	None(0b0000),
	Encryption(0b0001),
	Compression(0b0010),
	JsonSerialize(0b0100),
	JsonSerialize_Compression(0b0110)
	;
	
	static final PacketOptions[] OPTIONS = PacketOptions.values();
	public final int value;
	PacketOptions(int value) {
		this.value = value;
	}	

	public boolean hasFlag(PacketOptions options) {
		int bits = options.value;
		return (this.value & bits) != 0;
	}
	
	public static PacketOptions of(int value) {
		switch (value) {
	        case 0: return None;
	        case 1: return Encryption;
	        case 2: return Compression;
	        case 4: return JsonSerialize;
	        case 6: return JsonSerialize_Compression;
	        default : throw new RuntimeException("not exist PacketOptions - " + value);
		}
	}
		
}
