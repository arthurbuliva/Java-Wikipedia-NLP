/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

/**
 *
 * @author arthur
 */
public class SwahiliParser
{

    public static void main(String[] args)
    {
        final String TEXT = "waliwatumbuiza".toLowerCase();
        
        String personalPronoun = "";
        String tense = "";
        String suffix = "";
        String subject = "";
        String root = "";


        // Determine the personal pronouns
        // Get the first letter.
        String firstLetter = TEXT.substring(0, 1);

        switch (firstLetter)
        {
            case "a": //Third person singular
                personalPronoun = firstLetter;
                break;
            case "m": //Second person plural
                personalPronoun = firstLetter;
                break;
            case "u": //Second person singular
                personalPronoun = firstLetter;
                break;
            default:
                // Alternatively, get the first two letters.
                String secondLetter = TEXT.substring(0, 2);

                switch (secondLetter)
                {
                    case "ni": //First person singular
                        personalPronoun = secondLetter;
                        break;
                    case "tu": //First person plural
                        personalPronoun = secondLetter;
                        break;
                    case "wa": //Third person plural
                        personalPronoun = secondLetter;
                        break;
                    default:
                        throw new AssertionError();
                }
                break;
        }

        if (!personalPronoun.isEmpty())
        {
            // Now we remove the personal pronoun from the original String

            String noPronoun = TEXT.replaceFirst(personalPronoun, "");

            // Next we get the tenses by checking if the word contains either of:
            // First person singular ->  waLInipiga  waNAnipiga  waTAnipiga
            // First person plural ->  waLItupiga  waNAtupiga  waTAtupiga
            // Second person singular ->  waliKUpiga  waNAkupiga  waTAkupiga
            // Second person plural ->  waLIwapiga  waNAwapiga  waTAwapiga
            // Third person singular ->  waLImpiga  waNAmpiga  waTAmpiga
            // Third person plural ->  wawaLIwapiga  waNAwapiga  waTAwapiga
            String parsedString = noPronoun.substring(0, 2);

            switch (parsedString)
            {
                case "li":
                case "na":
                case "ta":
                    tense = parsedString;
                    break;
                default:
                    break;
            }

            if (!tense.isEmpty())
            {
                // Tenses detected

                String noPronounNoTense
                        = TEXT.replaceFirst(personalPronoun, "")
                        .replaceFirst(tense, "");

                // Determine the subject of the verb

                // First person singular ->  waliNIpiga  wanaNIpiga  wataNIpiga
                // First person plural ->  waliTUpiga  wanaTUpiga  wataTUpiga
                // Second person singular ->  waliKUpiga  wanaKUpiga  wataKUpiga
                // Second person plural ->  waliMpiga  wanaMpiga  wataMpiga
                // Third person singular ->  waliMpiga  wanaMpiga  wataMpiga
                // Third person plural ->  waliWApiga  wanaWApiga  wataWApiga
                subject = noPronounNoTense.substring(0, 1);

                switch (subject)
                {
                    case "m":
                        subject = "m";

                        break;

                    default:
                        subject = noPronounNoTense.substring(0, 2);

                        switch (subject)
                        {
                            case "ni":
                            case "tu":
                            case "wa":
                                subject = subject;
                                break;
                            default:
                                subject = ""; // No subject. Refers to self.
                        }

                        break;
                }

                // Now we check the two letters of suffix.
                String lastLetter = noPronounNoTense.substring(noPronounNoTense.length() - 2);

                switch (lastLetter)
                {
                    case "ka":
                        // Swahili for possibility of something happening
                    case "wa":
                        // Swahili for possibility of something being done on behalf of
                    case "ia":
                        // Swahili for possibility of something being done on behalf of
                        suffix = lastLetter;
                        break;
                    default:
                        // Try the last letter of suffix.
                        lastLetter = noPronounNoTense.substring(noPronounNoTense.length() - 1);

                        switch (lastLetter)
                        {
                            case "a":
                                // Swahili for possibility of something being done on behalf of
                                suffix = lastLetter;
                                break;
                            default:
                                throw new AssertionError();
                        }
                        break;
                }

                // We can get the possible root of the verb
                root = noPronounNoTense.replaceFirst(subject, "");

                System.out.println("Personal pronoun: " + personalPronoun);
                System.out.println("Tense: " + tense);
                System.out.println("Subject: " + subject);
                System.out.println("Root: " + root.replace(suffix, ""));
                System.out.println("Suffix: " + suffix);
                
            }
            else
            {
                System.out.println("Possibly not a Swahili verb.");
            }

        }
        else
        {
            System.out.println("Possibly not a Swahili verb.");
        }

    }
}
