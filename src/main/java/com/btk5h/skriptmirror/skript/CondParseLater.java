package com.btk5h.skriptmirror.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.util.Kleenean;
import com.btk5h.skriptmirror.ScriptLoaderState;
import com.btk5h.skriptmirror.util.SkriptUtil;
import org.bukkit.event.Event;

@Name("Parse Later")
@Description({"You can find more information about this here: https://tpgamesnl.gitbook.io/skript-reflect/advanced/experiments#deferred-parsing"})
public class CondParseLater extends Condition {
  static {
    Skript.registerCondition(CondParseLater.class, "\\(parse[d] later\\) <.+>");
  }

  private String statement;
  private ScriptLoaderState scriptLoaderState;
  private Statement parsedStatement;

  @Override
  public boolean check(Event e) {
    Condition parsedCondition = getParsedCondition();

    if (parsedCondition == null) {
      return false;
    }

    return parsedCondition.check(e);
  }

  @Override
  protected TriggerItem walk(Event e) {
    Statement parsedStatement = getParsedStatement();

    if (parsedStatement == null) {
      return null;
    }

    TriggerItem.walk(parsedStatement, e);

    return null;
  }

  @Override
  public String toString(Event e, boolean debug) {
    if (parsedStatement != null) {
      return "parsed later: " + parsedStatement.toString(e, debug);
    }

    return "not parsed yet: " + statement;
  }

  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    if (!Consent.Feature.DEFERRED_PARSING.hasConsent(SkriptUtil.getCurrentScript())) {
      Skript.error("This feature requires consent, because it is experimental.");
      return false;
    }

    statement = parseResult.regexes.get(0).group();
    scriptLoaderState = ScriptLoaderState.copyOfCurrentState();
    return true;
  }

  private Statement getParsedStatement() {
    if (parsedStatement == null) {
      scriptLoaderState.applyToCurrentState();
      parsedStatement = Statement.parse(statement,
          String.format("Could not parse condition/effect at runtime: %s", statement));

      if (parsedStatement == null) {
        return null;
      }

      parsedStatement.setNext(getNext());
      parsedStatement.setParent(getParent());
    }

    return parsedStatement;
  }

  private Condition getParsedCondition() {
    if (parsedStatement == null) {
      scriptLoaderState.applyToCurrentState();
      parsedStatement = Condition.parse(statement,
          String.format("Could not parse condition at runtime: %s", statement));

      if (parsedStatement == null) {
        return null;
      }

      parsedStatement.setNext(getNext());
      parsedStatement.setParent(getParent());
    }

    if (!(parsedStatement instanceof Condition)) {
      throw new IllegalStateException(
          String.format("%s was used as a condition but was parsed as a statement", statement));
    }

    return ((Condition) parsedStatement);
  }
}
