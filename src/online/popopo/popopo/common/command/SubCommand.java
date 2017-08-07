package online.popopo.popopo.common.command;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SubCommand implements Comparable<SubCommand> {
    private final String command;
    private final Method method;
    private final String[] argKeys;
    private final int size;
    private final int commandSize;

    public SubCommand(Method m) {
        Executor e = m.getAnnotation(Executor.class);
        String[] v = e.value();
        String s = String.join(" ", v).trim();

        this.command = v[0];
        this.method = m;
        this.argKeys = Arrays.copyOfRange(v, 1, v.length);
        this.size = s.isEmpty() ? 0 : s.split(" ").length;
        this.commandSize = this.size - v.length + 1;
    }

    public String getCommand() {
        return this.command;
    }

    public String[] getArgKeys() {
        return this.argKeys;
    }

    public int getSize() {
        return this.size;
    }

    public int getCommandSize() {
        return this.commandSize;
    }

    public boolean matchWith(String s) {
        return this.command.isEmpty() ||
                (s + " ").startsWith(this.command + " ");
    }

    public boolean resembleWith(String s) {
        return !this.command.isEmpty() &&
                this.command.startsWith(s);
    }

    public boolean run(Object o, CommandSender s, String[] a){
        Map<String, String> map = new HashMap<>();
        int start = this.commandSize;

        for (int i = start; i < a.length; i++) {
            map.put(this.argKeys[i - start], a[i]);
        }

        Argument arg = new Argument(s, map);

        try {
            return (Boolean) this.method .invoke(o, arg);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int compareTo(SubCommand c) {
        return this.size - c.size;
    }
}