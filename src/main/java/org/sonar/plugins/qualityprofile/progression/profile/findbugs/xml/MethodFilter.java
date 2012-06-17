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
/*    */ @XStreamAlias("Method")
/*    */ public class MethodFilter
/*    */ {
/*    */
/*    */   @XStreamAsAttribute
/*    */   private String name;
/*    */
/*    */   @XStreamAsAttribute
/*    */   private String params;
/*    */
/*    */   @XStreamAsAttribute
/*    */   private String returns;
/*    */
/*    */   public MethodFilter()
/*    */   {
/*    */   }
/*    */
/*    */   public MethodFilter(String name)
/*    */   {
/* 42 */     this.name = name;
/*    */   }
/*    */
/*    */   public String getName() {
/* 46 */     return this.name;
/*    */   }
/*    */
/*    */   public void setName(String name) {
/* 50 */     this.name = name;
/*    */   }
/*    */
/*    */   public String getParams() {
/* 54 */     return this.params;
/*    */   }
/*    */
/*    */   public void setParams(String params) {
/* 58 */     this.params = params;
/*    */   }
/*    */
/*    */   public String getReturns() {
/* 62 */     return this.returns;
/*    */   }
/*    */
/*    */   public void setReturns(String returns) {
/* 66 */     this.returns = returns;
/*    */   }
/*    */ }

/* Location:           C:\Users\David\AppData\Local\Temp\Rar$DIa0.109\
 * Qualified Name:     org.sonar.plugins.findbugs.xml.MethodFilter
 * JD-Core Version:    0.6.0
 */