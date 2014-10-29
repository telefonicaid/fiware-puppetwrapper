/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.puppetwrapper.auth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author fernandolopezaguilar
 */
public class CompareDates {

    private static Long limit;
    private long offset;
    private static Logger log = Logger.getLogger("CompareDates");

    /**
     * Constructor.
     * 
     * @param myLimit
     *            The limit param.
     */
    public CompareDates(Long myLimit) {
        limit = myLimit;
    }

    /**
     * Contructor.
     */
    public CompareDates() {
        limit = 0L;
    }

    /**
     * Check two dates in order to check if it is under or upper the limit.
     * 
     * @param dateString
     *            The date1 to compare.
     * @param now
     *            The current date and the date2
     * @return True if the date1-date2 is less than limit, false elsewhere.
     */
    public boolean checkDate(String dateString, Date now) {
        boolean result;

        Date date = this.getDate(dateString, getType(dateString)); // 2

        long diff = getTimeDiff(date, now) - offset;

        log.info("Date1: " + dateString + "\tDate2: " + now.toString());
        log.info("Diff: " + diff);

        if (!now.before(date)) {
            result = true;
        } else {
            result = false;
        }

        // if (diff > limit) {
        // result = false;
        // } else {
        // result = true;
        // }

        return result;
    }

    public void setLimit(Long myLimit) {
        limit = myLimit;
    }

    /**
     * Get diference of two dates.
     * 
     * @param dateOne
     *            The first date.
     * @param dateTwo
     *            The second date.
     * @return The different between dateOne and dateTwo in miliseconds.
     */
    public long getTimeDiff(Date dateOne, Date dateTwo) {
        long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
        return timeDiff;
    }

    /**
     * Get diference of two dates.
     * 
     * @param dateOne
     *            The first date.
     * @param dateTwo
     *            The second date.
     * @return The different between dateOne and dateTwo in miliseconds.
     */
    public long getTimeDiff(Date dateOne, String dateTwo) {
        return 1;
    }

    /**
     * Get diference of two dates.
     * 
     * @param dateOne
     *            The first date.
     * @param dateTwo
     *            The second date.
     * @return The different between dateOne and dateTwo in miliseconds.
     */
    public long getTimeDiff(String dateOne, Date dateTwo) {
        Date date1 = this.getDate(dateOne, getType(dateOne)); // 1);

        long timeDiff = date1.getTime() - dateTwo.getTime();

        return timeDiff;
    }

    /**
     * Get a date in a specific format.
     * 
     * @param dateString
     *            The date in string format
     * @param typeFormat
     *            The type format.
     * @return A object of class Date in the specific format.
     */
    public Date getDate(String dateString, int typeFormat) {
        Date date = null;

        switch (typeFormat) {
        case 0:
            // Format: "2012-11-29T18:00:45Z";
            try {
                XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);

                cal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                Calendar c2 = cal.toGregorianCalendar();

                date = c2.getTime();
            } catch (DatatypeConfigurationException ex) {
                log.warning("Cannot parse correctly the date: " + date);
            }

            break;

        case 1:
            // Format: Tue, 16 Aug 2011 19:50:26 GMT
            try {
                date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", new Locale("en_EN")).parse(dateString);

            } catch (ParseException ex) {
                log.warning("Cannot parse correctly the date: " + date);
            }

            break;

        case 2:
            // Format: Tue Dec 04 18:10:32 CET 2012
            try {
                date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("en_EN")).parse(dateString);

            } catch (ParseException ex) {
                log.warning("Cannot parse correctly the date: " + date);
            }

            break;

        default:
            throw new UnsupportedOperationException("Date format type not valid or not implemented");
        }

        return date;
    }

    /**
     * Validate two dates in string formats.
     * 
     * @param dateString1
     *            The date 1.
     * @param dateString2
     *            The date 2.
     * @return The deadline between the limit and the different of the two
     *         dates.
     */
    public String validateDates(String dateString1, String dateString2) {
        Date date1 = this.getDate(dateString1, getType(dateString1)); // 0);
        Date date2 = this.getDate(dateString2, getType(dateString2)); // 1);

        Long dateLong1 = date1.getTime();
        Long dateLong2 = date2.getTime();

        long timeDiff = dateLong1 - dateLong2;

        if (timeDiff != 86400000) {
            log.warning("Date format incorrect between token.expires " + "and Header field in the HTTP message");

            dateLong1 += (86400000 - timeDiff);
        }

        Date newdate = new Date(dateLong1);

        return newdate.toString();
    }

    public void setOffset(long myOffset) {
        this.offset = myOffset;
    }

    /**
     * Get the type format of date.
     * 
     * @param data
     *            The date in string format to be checked.
     * @return The type of the string date.
     */
    protected int getType(String data) {
        // Regular Expression [0-9]*\-[0-9]*\-[0-9]*T[0-9]*:[0-9]*:[0-9]*Z$
        // as a Java string "[0-9]*\\-[0-9]*\\-[0-9]*T[0-9]*:[0-9]*:[0-9]*Z$"
        // example: 2012-12-28T18:00:45Z
        String pattern = "[0-9]*\\-[0-9]*\\-[0-9]*T[0-9]*:[0-9]*:[0-9]*Z$";

        boolean matching = data.matches(pattern);

        int result = -1;

        if (matching) {
            result = 0;
        } else {
            // Regular Expression
            // [a-zA-Z]*\,\s*[0-9]*\s*[a-zA-Z]*\s*[0-9]*\s*[0-9]*:[0-9]*:[0-9]*\s*GMT$
            // as a Java string
            // "[a-zA-Z]*\\,\\s*[0-9]*\\s*[a-zA-Z]*\\s*[0-9]*\\s*[0-9]*:[0-9]*:[0-9]*\\s*GMT$"
            // example: Tue, 16 Aug 2011 19:50:26 GMT
            pattern = "[a-zA-Z]*\\,\\s*[0-9]*\\s*[a-zA-Z]*\\s*[0-9]*\\s*[0-9]*:[0-9]*:[0-9]*\\s*GMT$";

            matching = data.matches(pattern);

            if (matching) {
                result = 1;
            } else {
                // Regular Expression
                // [a-zA-Z]*\s*[a-zA-Z]*\s*[0-9]*\s*[0-9]*:[0-9]*:[0-9]*\s*CET\s*[0-9]*$
                // as a Java string
                // "[a-zA-Z]*\\s*[a-zA-Z]*\\s*[0-9]*\\s*[0-9]*:[0-9]*:[0-9]*\\s*CET\\s*[0-9]*$"
                // example: Tue Dec 04 18:10:32 CET 2012
                pattern = "[a-zA-Z]*\\s*[a-zA-Z]*\\s*[0-9]*\\s*[0-9]*:[0-9]*:[0-9]*\\s*CET\\s*[0-9]*$";

                matching = data.matches(pattern);

                if (matching) {
                    result = 2;
                } else {
                    // Regular Expression
                    // [a-zA-Z]*\s*[a-zA-Z]*\s*[0-9]*\s*[0-9]*:[0-9]*:[0-9]*\s*CEST\s*[0-9]*$
                    // as a Java string
                    // "[a-zA-Z]*\\s*[a-zA-Z]*\\s*[0-9]*\\s*[0-9]*:[0-9]*:[0-9]*\\s*CEST\\s*[0-9]*$"
                    // example: Tue Dec 04 18:10:32 CEST 2012
                    pattern = "[a-zA-Z]*\\s*[a-zA-Z]*\\s*[0-9]*\\s*[0-9]*:[0-9]*:[0-9]*\\s*CEST\\s*[0-9]*$";

                    matching = data.matches(pattern);

                    if (matching) {
                        result = 2;
                    }

                }
            }
        }

        return result;
    }
}
