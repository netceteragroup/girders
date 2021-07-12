package com.netcetera.girders.nca;

import com.google.common.base.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Utils class to parse, validate and calculate Netcetera project ids and their checksums.
 * <p>
 * A <b>complete</b> NCA project id has 3 parts divided by hyphens:
 * <blockquote><pre>
 * nca-099-9
 * |   |   +- Checksum part
 * |   +- Number part
 * +- Project tag part
 * </pre></blockquote>
 * A <b>partial</b> NCA project id has 2 parts divided by a hyphen:
 * <blockquote><pre>
 * nca-099
 * |   +- Number part
 * +- Project tag part
 * </pre></blockquote>
 * <p>
 * Note that this class was contributed by
 * <a href="mailto:thomas.marti@netcetera.com">Thomas Marti</a>.
 */
public final class NcaUtils {

  /**
   * Regex to match a complete nca project id.
   */
  public static final Pattern PROJECT_ID_PATTERN = Pattern.compile("[a-z]{3}-\\d{3}-\\d", Pattern.CASE_INSENSITIVE);
  /**
   * Regex to match an NCA project id inside a longer string. Used for parsing.
   */
  public static final Pattern PROJECT_ID_PATTERN_IN_LONGER_STRING = Pattern.compile(".*([a-z]{3}-\\d{3}(-\\d)?).*",
      Pattern.CASE_INSENSITIVE);
  /**
   * Regex to match just the project tag part of the id.
   */
  public static final Pattern PROJECT_TAG_PATTERN = Pattern.compile("[a-z]{3}", Pattern.CASE_INSENSITIVE);
  /**
   * Regex to match just the number part of the id.
   */
  public static final Pattern PROJECT_NUMBER_PATTERN = Pattern.compile("\\d{3}", Pattern.CASE_INSENSITIVE);

  private NcaUtils() {
  }

  /**
   * Finds the first valid <i>complete or partial</i> NCA project id inside a search string.
   *
   * @param searchString The string to search in, can be null or empty.
   *
   * @return The first NCA project id found or null, never empty.
   */
  public static String findProjectId(String searchString) {
    String searchStringValue = (searchString == null) ? "" : searchString.trim();
    Matcher matcher = PROJECT_ID_PATTERN_IN_LONGER_STRING.matcher(searchStringValue);
    if (matcher.matches()) {
      searchStringValue = matcher.group(1);

      if (matcher.group(2) != null) {
        if (isValidProjectId(searchStringValue)) {
          return searchStringValue;
        }
      } else {
        String[] split = searchStringValue.split("-");
        return completeProjectId(split[0], split[1]);
      }
    }

    return null;
  }

  /**
   * Returns true, if the given <tt>id</tt> is a valid <i>complete</i> Netcetera project id.
   *
   * @param id String to be tested.
   *
   * @return true if given a valid complete project id, false otherwise.
   */
  public static boolean isValidProjectId(String id) {
    if (Strings.isNullOrEmpty(id)) {
      return false;
    }
    if (!PROJECT_ID_PATTERN.matcher(id).matches()) {
      return false;
    }

    String[] split = id.split("-");
    return calculateProjectIdChecksum(split[0], split[1]).equals(split[2]);
  }

  /**
   * Returns the <i>complete</i> NCA project id for the given project tag and number.
   *
   * @param tag    The company part of the project id.
   * @param number The number of the project.
   *
   * @return A valid NCA project id in this format: xyz-012-3
   *
   * @throws NullPointerException     if either <tt>tag</tt> or <tt>number</tt> is <tt>null</tt>.
   * @throws IllegalArgumentException if either <tt>tag</tt> or <tt>number</tt> have the wrong
   *                                  format.
   */
  public static String completeProjectId(String tag, String number)
      throws NullPointerException, IllegalArgumentException {
    return tag + '-' + number + '-' + calculateProjectIdChecksum(tag, number);
  }

  /**
   * Calculates the checksum for the given project tag and number.
   *
   * @param tag    The company part of the project id, format: <tt>[a-zA-Z]{3}</tt>.
   * @param number The number of the project, format: <tt>[0-9]{3}</tt>.
   *
   * @return The project id checksum, a string with length <tt>1</tt>.
   *
   * @throws NullPointerException     if either <tt>tag</tt> or <tt>number</tt> is <tt>null</tt>.
   * @throws IllegalArgumentException if either <tt>tag</tt> or <tt>number</tt> have the wrong
   *                                  format.
   */
  @SuppressWarnings({"StandardVariableNames", "CharUsedInArithmeticContext"})
  public static String calculateProjectIdChecksum(String tag, String number)
      throws NullPointerException, IllegalArgumentException {
    /*
     * The original TCL code:
     * proc createProjectNumberMagic {tag number} {
     *     # code from mf
     *     # whatever it does, it does it right
     *     set prjowner [string toupper $tag]
     *     scan $prjowner "%c%c%c" prjowner1 prjowner2 prjowner3
     *
     *     set prjnum [string toupper $number]
     *     scan $prjnum "%1s%1s%1s" pn1 pn2 pn3
     *
     *     if {![regexp {[0-9]} $pn1]} {scan $pn1 "%c" pn1}
     *     if {![regexp {[0-9]} $pn2]} {scan $pn2 "%c" pn2}
     *     if {![regexp {[0-9]} $pn3]} {scan $pn3 "%c" pn3}
     *     set prj "$prjowner1$prjowner2$prjowner3$pn1$pn2$pn3"
     *
     *     set checkarray "0946827135"
     *     set takeover 0
     *     set prjlen [string length $prj]
     *
     *     for {set i 0} {$i<$prjlen} {incr i} {
     *       set checkarrayindex [expr ([string index $prj $i] + $takeover) % 10]
     *       set takeover [string index $checkarray $checkarrayindex]
     *     }
     *
     *     set takeover [expr (10 - $takeover) % 10]
     *
     *     return "$prjowner-$prjnum-$takeover"
     * }
     */
    String tagValue = tag.toUpperCase();

    if (!PROJECT_TAG_PATTERN.matcher(tagValue).matches()) {
      throw new IllegalArgumentException("Invalid value for tag: " + tagValue);
    }
    if (!PROJECT_NUMBER_PATTERN.matcher(number).matches()) {
      throw new IllegalArgumentException("Invalid value for number: " + number);
    }
    StringBuilder project = new StringBuilder();
    for (int c : tagValue.toCharArray()) {
      project.append(c);
    }

    project.append(number);

    int[] checkarray = {0, 9, 4, 6, 8, 2, 7, 1, 3, 5};
    int takeover = 0;
    for (int i : project.toString().toCharArray()) {
      takeover = checkarray[((i + takeover) - '0') % 10];
    }

    takeover = (10 - takeover) % 10;

    return String.valueOf(takeover);
  }


  /**
   * Compose an Infostore compliant "stamp" for a record.
   * Format is {@code unixname-time_since_epoch_in_s}.
   *
   * @param unixname The unix username of the user performing the operation.
   *
   * @return The created stamp.
   */
  public static String createInfostoreStamp(String unixname) {
    checkArgument(!Strings.isNullOrEmpty(unixname), "username can't be null or empty");
    return unixname + '-' + (System.currentTimeMillis() / 1000L);
  }

}
