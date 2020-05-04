package vinyarion.fukkit.main.game.jingle;

import vinyarion.fukkit.main.util.Misc;

public enum Pitch {
	
	Cn_m1(8.176), 
	Cs_m1(8.662), 
	Dn_m1(9.177), 
	Ds_m1(9.723), 
	En_m1(10.301), 
	Fn_m1(10.914), 
	Fs_m1(11.563), 
	Gn_m1(12.250), 
	Gs_m1(12.979), 
	An_m1(13.750), 
	As_m1(14.568), 
	Bn_m1(15.434), 

	Cn_0(Cn_m1.pitch() * 2), 
	Cs_0(Cs_m1.pitch() * 2), 
	Dn_0(Dn_m1.pitch() * 2), 
	Ds_0(Ds_m1.pitch() * 2), 
	En_0(En_m1.pitch() * 2), 
	Fn_0(Fn_m1.pitch() * 2), 
	Fs_0(Fs_m1.pitch() * 2), 
	Gn_0(Gn_m1.pitch() * 2), 
	Gs_0(Gs_m1.pitch() * 2), 
	An_0(An_m1.pitch() * 2), 
	As_0(As_m1.pitch() * 2), 
	Bn_0(Bn_m1.pitch() * 2), 

	Cn_1(Cn_m1.pitch() * 4), 
	Cs_1(Cs_m1.pitch() * 4), 
	Dn_1(Dn_m1.pitch() * 4), 
	Ds_1(Ds_m1.pitch() * 4), 
	En_1(En_m1.pitch() * 4), 
	Fn_1(Fn_m1.pitch() * 4), 
	Fs_1(Fs_m1.pitch() * 4), 
	Gn_1(Gn_m1.pitch() * 4), 
	Gs_1(Gs_m1.pitch() * 4), 
	An_1(An_m1.pitch() * 4), 
	As_1(As_m1.pitch() * 4), 
	Bn_1(Bn_m1.pitch() * 4), 

	Cn_2(Cn_m1.pitch() * 8), 
	Cs_2(Cs_m1.pitch() * 8), 
	Dn_2(Dn_m1.pitch() * 8), 
	Ds_2(Ds_m1.pitch() * 8), 
	En_2(En_m1.pitch() * 8), 
	Fn_2(Fn_m1.pitch() * 8), 
	Fs_2(Fs_m1.pitch() * 8), 
	Gn_2(Gn_m1.pitch() * 8), 
	Gs_2(Gs_m1.pitch() * 8), 
	An_2(An_m1.pitch() * 8), 
	As_2(As_m1.pitch() * 8), 
	Bn_2(Bn_m1.pitch() * 8), 

	Cn_3(Cn_m1.pitch() * 16), 
	Cs_3(Cs_m1.pitch() * 16), 
	Dn_3(Dn_m1.pitch() * 16), 
	Ds_3(Ds_m1.pitch() * 16), 
	En_3(En_m1.pitch() * 16), 
	Fn_3(Fn_m1.pitch() * 16), 
	Fs_3(Fs_m1.pitch() * 16), 
	Gn_3(Gn_m1.pitch() * 16), 
	Gs_3(Gs_m1.pitch() * 16), 
	An_3(An_m1.pitch() * 16), 
	As_3(As_m1.pitch() * 16), 
	Bn_3(Bn_m1.pitch() * 16), 

	Cn_4(Cn_m1.pitch() * 32), 
	Cs_4(Cs_m1.pitch() * 32), 
	Dn_4(Dn_m1.pitch() * 32), 
	Ds_4(Ds_m1.pitch() * 32), 
	En_4(En_m1.pitch() * 32), 
	Fn_4(Fn_m1.pitch() * 32), 
	Fs_4(Fs_m1.pitch() * 32), 
	Gn_4(Gn_m1.pitch() * 32), 
	Gs_4(Gs_m1.pitch() * 32), 
	An_4(An_m1.pitch() * 32), 
	As_4(As_m1.pitch() * 32), 
	Bn_4(Bn_m1.pitch() * 32), 

	Cn_5(Cn_m1.pitch() * 64), 
	Cs_5(Cs_m1.pitch() * 64), 
	Dn_5(Dn_m1.pitch() * 64), 
	Ds_5(Ds_m1.pitch() * 64), 
	En_5(En_m1.pitch() * 64), 
	Fn_5(Fn_m1.pitch() * 64), 
	Fs_5(Fs_m1.pitch() * 64), 
	Gn_5(Gn_m1.pitch() * 64), 
	Gs_5(Gs_m1.pitch() * 64), 
	An_5(An_m1.pitch() * 64), 
	As_5(As_m1.pitch() * 64), 
	Bn_5(Bn_m1.pitch() * 64), 

	Cn_6(Cn_m1.pitch() * 128), 
	Cs_6(Cs_m1.pitch() * 128), 
	Dn_6(Dn_m1.pitch() * 128), 
	Ds_6(Ds_m1.pitch() * 128), 
	En_6(En_m1.pitch() * 128), 
	Fn_6(Fn_m1.pitch() * 128), 
	Fs_6(Fs_m1.pitch() * 128), 
	Gn_6(Gn_m1.pitch() * 128), 
	Gs_6(Gs_m1.pitch() * 128), 
	An_6(An_m1.pitch() * 128), 
	As_6(As_m1.pitch() * 128), 
	Bn_6(Bn_m1.pitch() * 128), 

	Cn_7(Cn_m1.pitch() * 256), 
	Cs_7(Cs_m1.pitch() * 256), 
	Dn_7(Dn_m1.pitch() * 256), 
	Ds_7(Ds_m1.pitch() * 256), 
	En_7(En_m1.pitch() * 256), 
	Fn_7(Fn_m1.pitch() * 256), 
	Fs_7(Fs_m1.pitch() * 256), 
	Gn_7(Gn_m1.pitch() * 256), 
	Gs_7(Gs_m1.pitch() * 256), 
	An_7(An_m1.pitch() * 256), 
	As_7(As_m1.pitch() * 256), 
	Bn_7(Bn_m1.pitch() * 256), 

	Cn_8(Cn_m1.pitch() * 512), 
	Cs_8(Cs_m1.pitch() * 512), 
	Dn_8(Dn_m1.pitch() * 512), 
	Ds_8(Ds_m1.pitch() * 512), 
	En_8(En_m1.pitch() * 512), 
	Fn_8(Fn_m1.pitch() * 512), 
	Fs_8(Fs_m1.pitch() * 512), 
	Gn_8(Gn_m1.pitch() * 512), 
	Gs_8(Gs_m1.pitch() * 512), 
	An_8(An_m1.pitch() * 512), 
	As_8(As_m1.pitch() * 512), 
	Bn_8(Bn_m1.pitch() * 512), 

	Cn_9(Cn_m1.pitch() * 1024), 
	Cs_9(Cs_m1.pitch() * 1024), 
	Dn_9(Dn_m1.pitch() * 1024), 
	Ds_9(Ds_m1.pitch() * 1024), 
	En_9(En_m1.pitch() * 1024), 
	Fn_9(Fn_m1.pitch() * 1024), 
	Fs_9(Fs_m1.pitch() * 1024), 
	Gn_9(Gn_m1.pitch() * 1024), 
//	Gs_9(Gs_m1.pitch() * 1024), 
//	An_9(An_m1.pitch() * 1024), 
//	As_9(As_m1.pitch() * 1024), 
//	Bn_9(Bn_m1.pitch() * 1024), 
	;
	
	public static Pitch from(String s) {
		return Misc.isInt(s) ? fromMIDI(Integer.parseInt(s)) : fromText(s);
	}
	
	public static Pitch fromText(String text) {
		return Pitch.valueOf(text);
	}
	
	public static Pitch fromMIDI(int midi) {
		return values()[midi];
	}

	private Pitch(double pitch) {
		this.pitch = pitch;
	}
	
	private final double pitch;
	
	public final double diff(Pitch pitch) {
		return this.pitch / pitch.pitch;
	}
	
	public final double pitch() {
		return this.pitch;
	}
	
}
