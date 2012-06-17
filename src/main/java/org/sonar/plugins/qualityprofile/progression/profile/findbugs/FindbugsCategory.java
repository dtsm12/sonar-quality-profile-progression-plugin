/*
 * Quality Profile Progression
 * Copyright (C) 2012 David T S Maitland
 * david.ts.maitland@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
/*    */ package org.sonar.plugins.qualityprofile.progression.profile.findbugs;
/*    */
/*    */ import java.util.HashMap;
import java.util.Map;
/*    */
/*    */ public final class FindbugsCategory
/*    */ {
/* 26 */   private static final Map<String, String> FINDBUGS_TO_SONAR = new HashMap<String, String>();
/*    */
/*    */   public static String findbugsToSonar(String findbugsCategKey)
/*    */   {
/* 41 */     return (String)FINDBUGS_TO_SONAR.get(findbugsCategKey);
/*    */   }
/*    */
/*    */   static
/*    */   {
/* 29 */     FINDBUGS_TO_SONAR.put("BAD_PRACTICE", "Bad practice");
/* 30 */     FINDBUGS_TO_SONAR.put("CORRECTNESS", "Correctness");
/* 31 */     FINDBUGS_TO_SONAR.put("MT_CORRECTNESS", "Multithreaded correctness");
/* 32 */     FINDBUGS_TO_SONAR.put("I18N", "Internationalization");
/* 33 */     FINDBUGS_TO_SONAR.put("EXPERIMENTAL", "Experimental");
/* 34 */     FINDBUGS_TO_SONAR.put("MALICIOUS_CODE", "Malicious code");
/* 35 */     FINDBUGS_TO_SONAR.put("PERFORMANCE", "Performance");
/* 36 */     FINDBUGS_TO_SONAR.put("SECURITY", "Security");
/* 37 */     FINDBUGS_TO_SONAR.put("STYLE", "Style");
/*    */   }
/*    */ }

/* Location:           C:\Users\David\AppData\Local\Temp\Rar$DIa0.463\
 * Qualified Name:     org.sonar.plugins.findbugs.FindbugsCategory
 * JD-Core Version:    0.6.0
 */