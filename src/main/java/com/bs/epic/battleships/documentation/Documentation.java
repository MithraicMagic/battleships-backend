package com.bs.epic.battleships.documentation;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.documentation.annotations.OnError;
import com.bs.epic.battleships.documentation.annotations.OnErrors;
import com.bs.epic.battleships.documentation.annotations.Returns;
import com.bs.epic.battleships.events.ErrorEvent;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;

public class Documentation {
    private final Collection<SocketEntry> socketApi;
    private final Collection<RestEntries> restApi;

    private SocketIOServer server;

    private static Documentation documentation = new Documentation();

    private Documentation() {
        this.socketApi = new ArrayList<>();
        this.restApi = new ArrayList<>();
    }

    public static Documentation get() { return documentation; }

    public <T, U> void addEventListener(String eventName, Class<T> eventClass, Class<U> result, DataListener<T> listener) {
        server.addEventListener(eventName, eventClass, listener);
        System.out.println("[Docs] Adding Listener on: " + eventName);

        var entry = new SocketEntry(eventName);
        entry.input = getFields(eventClass);
        if (result != null) {
            entry.output = getFields(result);
        }
        else {
            entry.output = new Fields(eventName);
        }
        entry.onError = getFields(ErrorEvent.class);

        socketApi.add(entry);
    }

    public <T> void addController(Class<T> controller) {
        var name = getName(controller.getName());
        var entries = new RestEntries(name);
        System.out.println("[Docs] Adding Controller with name: " + name);

        for (var an : controller.getAnnotations()) {
            if (an instanceof RequestMapping) {
                var reqAn = (RequestMapping) an;
                var val = reqAn.value();
                if (val.length != 0) {
                    entries.basePath = val[0];
                    break;
                }
            }
        }

        for (var method : controller.getMethods()) {
            RestEntry entry = null;

            for (var annotation : method.getAnnotations()) {
                if (annotation instanceof RequestMapping) {
                    var req = (RequestMapping) annotation;
                    entry = new RestEntry();

                    entry.httpVerb = req.method()[0].name();
                    entry.path = req.path()[0];
                    entry.output = new RestOutput(200);
                }

                if (entry != null) {
                    if (annotation instanceof Returns) {
                        var ret = (Returns) annotation;
                        entry.output.fields = getTuples(ret.value());
                    }

                    if (annotation instanceof OnError) {
                        var err = (OnError) annotation;
                        var code = err.code() == 0 ? 500 : err.code();
                        entry.onError.add(new RestOutput(code, err.desc(), getTuples(err.value())));
                    }

                    if (annotation instanceof OnErrors) {
                        var errors = (OnErrors) annotation;
                        for (var err : errors.value()) {
                            var code = err.code() == 0 ? 500 : err.code();
                            entry.onError.add(new RestOutput(code, err.desc(), getTuples(err.value())));
                        }
                    }
                }
            }

            if (entry != null) {
                for (var param : method.getParameters()) {
                    for (var annotation : param.getAnnotations()) {
                        if (annotation instanceof PathVariable) {
                            var type = param.getType().getName();
                            var n = param.getName();
                            var desc = param.getAnnotation(Doc.class).value();

                            entry.pathVariables.add(new Tuple(type, n, desc));
                        }

                        if (annotation instanceof RequestBody) {
                            entry.body = getFields(param.getType());
                        }
                    }
                }

                entries.entries.add(entry);
            }
        }

        restApi.add(entries);
    }

    private <T> Fields getFields(Class<T> c) {
        return new Fields(getTuples(c));
    }

    private <T> Collection<Tuple> getTuples(Class<T> c) {
        var col = new ArrayList<Tuple>();

        for (var field : c.getFields()) {
            var annotations = field.getDeclaredAnnotations();

            for (var annotation : annotations) {
                if (annotation instanceof Doc) {
                    var a = (Doc) annotation;
                    var name = "";

                    var gType = field.getGenericType();
                    if (gType instanceof ParameterizedType) {
                        var paramType = (ParameterizedType) gType;
                        var args = paramType.getActualTypeArguments();
                        for (var arg : args) {
                            var cArg = (Class) arg;
                            name += "<" + getName(cArg.getName()) + ">";
                        }
                    }
                    else {
                        name = getName(field.getType().toString());
                    }

                    col.add(new Tuple(
                        name,
                        field.getName(),
                        a.value()
                    ));
                }
            }
        }

        return col;
    }

    public Collection<SocketEntry> getSocketApi() { return socketApi; }
    public Collection<RestEntries> getRestApi() { return restApi; }

    public String getName(String n) {
        var split = n.split("\\.");
        return split[split.length - 1];
    }

    public void setSocketServer(SocketIOServer server) { this.server = server; }
}
