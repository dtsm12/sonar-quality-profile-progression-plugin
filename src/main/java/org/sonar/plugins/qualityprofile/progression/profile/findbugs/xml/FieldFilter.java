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
/*    */ @XStreamAlias("Field")
/*    */ public class FieldFilter
/*    */ {
/*    */
/*    */   @XStreamAsAttribute
/*    */   private String name;
/*    */
/*    */   @XStreamAsAttribute
/*    */   private String type;
/*    */
/*    */   public FieldFilter()
/*    */   {
/*    */   }
/*    */
/*    */   public FieldFilter(String name)
/*    */   {
/* 39 */     this.name = name;
/*    */   }
/*    */
/*    */   public String getName() {
/* 43 */     return this.name;
/*    */   }
/*    */
/*    */   public void setName(String name) {
/* 47 */     this.name = name;
/*    */   }
/*    */
/*    */   public String getType() {
/* 51 */     return this.type;
/*    */   }
/*    */
/*    */   public void setType(String type) {
/* 55 */     this.type = type;
/*    */   }
/*    */ }

/* Location:           C:\Users\David\AppData\Local\Temp\Rar$DIa0.755\
 * Qualified Name:     org.sonar.plugins.findbugs.xml.FieldFilter
 * JD-Core Version:    0.6.0
 */