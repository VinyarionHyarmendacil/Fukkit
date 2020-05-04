package vinyarion.fukkit.main.game.jingle;

public enum FSound {
	
	
	;
	private FSound(Pitch absolutePitch, String name) {
		this.absolutePitch = absolutePitch;
		this.name = name;
	}
	
	public final Pitch absolutePitch;
	public final String name;
	
}
