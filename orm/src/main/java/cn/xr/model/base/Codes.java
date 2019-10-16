package cn.xr.model.base;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import cn.xr.model.Codeable;
import cn.xr.model.Treeable;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class Codes
{
  public static final String SEPARATOR = ".";
private static final BASE64Encoder BASE64Encoder = new BASE64Encoder();

  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  public static String uuidAsBase64() {
    UUID uuid = UUID.randomUUID();

    ByteBuffer buf = ByteBuffer.wrap(new byte[16]);
    buf.putLong(uuid.getMostSignificantBits());
    buf.putLong(uuid.getLeastSignificantBits());

	String base64Str = BASE64Encoder.encode(buf.array());

    return CodesObject.trimEnd(base64Str, "=");
  }

  public static String generateQueryCode(Codeable entity)
  {
    Preconditions.checkNotNull(entity);

    if ((entity instanceof Treeable)) {
      return Joiner.on(".").join(CodesObject.fullCodeWithTreeable(entity));
    }

    return entity.getCode();
  }
  
  @SuppressWarnings({"unchecked","rawtypes"})
  private static class CodesObject
  {
  
	static List<String> fullCodeWithTreeable(Codeable entity) {
	List codes = Lists.newArrayList();
      Treeable it = (Treeable)entity;
      while (it != null) {
        codes.add(((Codeable)it).getCode());
        it = (Treeable)it.getParent();
      }

      return Lists.reverse(codes);
    }

    static String trimEnd(String from, String trimStr) {
      if (from == null) {
        return "";
      }
      if (trimStr == null) {
        return from;
      }

      String result = from;
      while (result.endsWith(trimStr)) {
        result = result.substring(0, result.length() - trimStr.length());
      }
      return result;
    }
  }
}