package com.omisoft.hsracer.common.structures;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

/**
 * Created by develope on 08/12/16.
 */

class SecureStringDeserializer extends StdDeserializer<SecureString> {

  protected SecureStringDeserializer(Class<?> vc) {
    super(vc);
  }

  public SecureStringDeserializer() {
    this(null);
  }


  @Override
  public SecureString deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {

    Character[] characterArray = jp.getCodec().readValue(jp, Character[].class);
    char[] charBuffer = new char[characterArray.length];
    int i = 0;
    for (Character c : characterArray) {
      charBuffer[i++] = c;
    }

    return new SecureString(charBuffer);
  }
}
