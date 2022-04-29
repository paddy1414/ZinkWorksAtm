package org.patricknorton.zinkworks.ZinkWorksAtm;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class test {
    LinkedHashMap<String, Integer> atmNoteBalance = new LinkedHashMap<>();


    private void reFillAtm() {
        atmNoteBalance.put("50", 10);
        atmNoteBalance.put("20", 30);
        atmNoteBalance.put("10", 30);
        atmNoteBalance.put("5", 20);
    }

    public LinkedHashMap calculateNotesRemaining(int amountToWidraw) {
        AtomicInteger remainder = new AtomicInteger(amountToWidraw);
        StringBuilder sb = new StringBuilder();
        LinkedHashMap<String, Integer> notesWidrawn = new LinkedHashMap<>();
        atmNoteBalance.keySet().stream().forEach(k -> {
            int keyAsInt = Integer.parseInt(k);
            int notesToRemove;
            if (remainder.get() % keyAsInt > 1 && atmNoteBalance.get(k) > 0) {
                notesToRemove = remainder.get() / keyAsInt;
                atmNoteBalance.put(k, atmNoteBalance.get(k) - notesToRemove);
                remainder.set(remainder.get() % keyAsInt);
                if(notesToRemove!=0) {
                    notesWidrawn.put(k, notesToRemove);
                }
                sb.append(String.format("%s euro notes: %d \n", k, notesToRemove));
            }
        });

        sb.append(String.format("NOTE: %d euro is too small for us to widraw", remainder.get()));

        System.out.println(sb);
        return notesWidrawn;
    }

    public void somethingElse(LinkedHashMap<String, Integer> atmNotesRemoved) {
        final int[] openingBalance = {0};
        atmNotesRemoved.keySet().forEach(k-> {
            int keyValue = Integer.parseInt(k);
        openingBalance[0] = openingBalance[0] + (atmNotesRemoved.get(k).intValue() * keyValue);
        });
        System.out.println(openingBalance[0]);
    }

    public static void main(String[] args) {
        test testClass = new test();
        testClass.reFillAtm();
        //  testClass.blank();

       testClass.somethingElse(testClass.calculateNotesRemaining(127));

        System.out.println(1%4);
    }
}
