package com.btk5h.skriptmirror.skript.reflect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.util.Kleenean;
import com.btk5h.skriptmirror.WrappedEvent;
import org.bukkit.event.Event;

@Name("Event")
@Description({"You can find more information about this here: https://tpgamesnl.gitbook.io/skript-reflect/basics/handling-events#using-the-event-expression"})
public class ExprEvent extends SimpleExpression<Event> {
  static {
    Skript.registerExpression(ExprEvent.class, Event.class, ExpressionType.SIMPLE, "[the] event");
  }

  @Override
  protected Event[] get(Event e) {
    if (e instanceof WrappedEvent) {
      return new Event[]{((WrappedEvent) e).getEvent()};
    }
    return new Event[]{e};
  }

  @Override
  public boolean isSingle() {
    return true;
  }

  @Override
  public Class<? extends Event> getReturnType() {
    return Event.class;
  }

  @Override
  public String toString(Event e, boolean debug) {
    if (e == null) {
      return "the event";
    }
    return e.getEventName();
  }

  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    return true;
  }
}
