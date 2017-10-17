package xdean.reflect.getter;

import java.nio.file.Path;
import java.nio.file.Paths;

import xdean.jex.internal.codecov.CodecovIgnoreHandler;

public class CodecovUpdater {
  public static void main(String[] args) {
    Path codecov = Paths.get("codecov.yml");
    Path path = Paths.get("src", "main", "java");
    CodecovIgnoreHandler.updateCodecovIgnore(codecov, path);
  }
}
