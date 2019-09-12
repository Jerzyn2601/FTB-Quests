package com.feed_the_beast.ftbquests.integration.kubejs;

import com.feed_the_beast.ftbquests.events.ObjectCompletedEvent;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.server.ServerJS;

/**
 * @author LatvianModder
 */
@DocClass("Event that gets fired when an object is completed. It can be a file, quest, chapter, task")
public class QuestObjectCompletedEventJS extends EventJS
{
	public final transient ObjectCompletedEvent event;

	public QuestObjectCompletedEventJS(ObjectCompletedEvent e)
	{
		event = e;
	}

	@DocMethod
	public QuestData getData()
	{
		return event.getData();
	}

	@DocMethod
	public QuestObject getObject()
	{
		return event.getObject();
	}

	@DocMethod("List of notified players. It isn't always the list of online members of that team, for example, this list is empty when invisible quest was completed")
	public EntityArrayList getNotifiedPlayers()
	{
		return ServerJS.instance.getOverworld().createEntityList(event.getNotifiedPlayers());
	}

	@DocMethod("List of all online team members")
	public EntityArrayList getOnlineMembers()
	{
		return ServerJS.instance.getOverworld().createEntityList(getData().getOnlineMembers());
	}
}