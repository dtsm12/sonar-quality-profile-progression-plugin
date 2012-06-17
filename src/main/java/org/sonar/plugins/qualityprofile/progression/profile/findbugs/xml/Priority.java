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
/*    */ package org.sonar.plugins.qualityprofile.progression.profile.findbugs.xml;
/*    */
/*    */ import com.thoughtworks.xstream.annotations.XStreamAlias;
/*    */ import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
/*    */
/*    */ @XStreamAlias("Priority")
/*    */ public class Priority
/*    */ {
/*    */
/*    */   @XStreamAsAttribute
/*    */   private String value;
/*    */
/*    */   public Priority()
/*    */   {
/*    */   }
/*    */
/*    */   public Priority(String value)
/*    */   {
/* 35 */     this.value = value;
/*    */   }
/*    */
/*    */   public String getValue() {
/* 39 */     return this.value;
/*    */   }
/*    */
/*    */   public void setValue(String value) {
/* 43 */     this.value = value;
/*    */   }
/*    */ }

/* Location:           C:\Users\David\AppData\Local\Temp\Rar$DIa0.275\
 * Qualified Name:     org.sonar.plugins.findbugs.xml.Priority
 * JD-Core Version:    0.6.0
 */