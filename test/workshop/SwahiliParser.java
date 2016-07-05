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
        String text = "walitambua";
        String personalPronoun = "";
        String tense = "";
        String suffix = "";
        String root = "";

        text = text.toLowerCase();

        // Determine the personal pronouns
        // Get the first letter.
        String firstLetter = text.substring(0, 1);

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
                String secondLetter = text.substring(0, 2);

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

            String noPronoun = text.replaceFirst(personalPronoun, "");

            // Next we get the tenses by checking if the word contains either of:
            // -li-  -na-  -ta-
            String parsedString = noPronoun.substring(0, 2);

            switch (parsedString)
            {
                case "li":
                    tense = parsedString;
                    break;
                case "na":
                    tense = parsedString;
                    break;
                case "ta":
                    tense = parsedString;
                    break;
                default:
                    throw new AssertionError();
            }

            if (!tense.isEmpty())
            {
                // Tenses detected
                System.out.println("Possible verb detected");

                // Now we check the two letters of suffix.
                String lastLetter = noPronoun.substring(noPronoun.length() - 2);

                switch (lastLetter)
                {
                    case "ka":
                        // Swahili for possibility of something happening
                        suffix = lastLetter;
                        break;
                    case "wa":
                        // Swahili for possibility of something being done on behalf of
                        suffix = lastLetter;
                        break;
                    default:
                        // Try the last letter of suffix.
                        lastLetter = noPronoun.substring(noPronoun.length() - 1);

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
                root = noPronoun.replaceFirst(tense, "");
                
                System.out.println("Personal pronoun: " + personalPronoun);
                System.out.println("Tense: " + tense);
                System.out.println("Suffix: " + suffix);
                System.out.println("Root: " + root);
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
