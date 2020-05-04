package vinyarion.lotrclientutil.common.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public interface IHandler {

	public void handle(IMessage message, MessageContext ctx);

}
