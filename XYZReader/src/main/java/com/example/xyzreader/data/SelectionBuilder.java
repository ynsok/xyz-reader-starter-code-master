/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications:
 * -Imported from AOSP frameworks/base/core/java/com/android/internal/content
 * -Changed package name
 */

package com.example.xyzreader.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Helper for building selection clauses for {@link SQLiteDatabase}. Each
 * appended clause is combined using {@code AND}. This class is <em>not</em>
 * thread safe.
 */
public class SelectionBuilder {
    private String mTable = null;
    // --Commented out by Inspection (21.05.2018 22:40):private HashMap<String, String> mProjectionMap;
    private StringBuilder mSelection;
    private ArrayList<String> mSelectionArgs;

// --Commented out by Inspection START (21.05.2018 13:32):
//    /**
//     * Reset any internal state, allowing this builder to be recycled.
//     */
//    public SelectionBuilder reset() {
//        mTable = null;
//		if (mProjectionMap != null) {
//			mProjectionMap.clear();
//		}
//		if (mSelection != null) {
//			mSelection.setLength(0);
//		}
//		if (mSelectionArgs != null) {
//			mSelectionArgs.clear();
//		}
//        return this;
//    }
// --Commented out by Inspection STOP (21.05.2018 13:32)

    /**
     * Append the given selection clause to the internal state. Each clause is
     * surrounded with parenthesis and combined using {@code AND}.
     */
    public SelectionBuilder where(String selection, String... selectionArgs) {
        if (TextUtils.isEmpty(selection)) {
            if (selectionArgs != null && selectionArgs.length > 0) {
                throw new IllegalArgumentException(
                        "Valid selection required when including arguments=");
            }

            // Shortcut when clause is empty
            return this;
        }

        ensureSelection(selection.length());
        if (mSelection.length() > 0) {
            mSelection.append(" AND ");
        }

        mSelection.append("(").append(selection).append(")");
        if (selectionArgs != null) {
        	ensureSelectionArgs();
            Collections.addAll(mSelectionArgs, selectionArgs);
        }

        return this;
    }

    public SelectionBuilder table(String table) {
        mTable = table;
        return this;
    }

    private void assertTable() {
        if (mTable == null) {
            throw new IllegalStateException("Table not specified");
        }
    }

// --Commented out by Inspection START (21.05.2018 13:39):
//    private void ensureProjectionMap() {
//		if (mProjectionMap == null) {
//			mProjectionMap = new HashMap<>();
//		}
//    }
// --Commented out by Inspection STOP (21.05.2018 13:39)

    private void ensureSelection(int lengthHint) {
    	if (mSelection == null) {
    		mSelection = new StringBuilder(lengthHint + 8);
    	}
    }

    private void ensureSelectionArgs() {
    	if (mSelectionArgs == null) {
    		mSelectionArgs = new ArrayList<>();
    	}
    }

// --Commented out by Inspection START (21.05.2018 13:32):
//    public SelectionBuilder mapToTable(String column, String table) {
//    	ensureProjectionMap();
//        mProjectionMap.put(column, table + "." + column);
//        return this;
//    }
// --Commented out by Inspection STOP (21.05.2018 13:32)

// --Commented out by Inspection START (21.05.2018 13:32):
//    public SelectionBuilder map(String fromColumn, String toClause) {
//    	ensureProjectionMap();
//        mProjectionMap.put(fromColumn, toClause + " AS " + fromColumn);
//        return this;
//    }
// --Commented out by Inspection STOP (21.05.2018 13:32)

    /**
     * Return selection string for current internal state.
     *
     * @see #getSelectionArgs()
     */
    private String getSelection() {
    	if (mSelection != null) {
            return mSelection.toString();
    	} else {
    		return null;
    	}
    }

    /**
     * Return selection arguments for current internal state.
     *
     * @see #getSelection()
     */
    private String[] getSelectionArgs() {
    	if (mSelectionArgs != null) {
            return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
    	} else {
    		return null;
    	}
    }

    private void mapColumns(String[] columns) {
    	if (mProjectionMap == null) return;
        for (int i = 0; i < columns.length; i++) {
            final String target = mProjectionMap.get(columns[i]);
            if (target != null) {
                columns[i] = target;
            }
        }
    }

    @Override
    public String toString() {
        return "SelectionBuilder[table=" + mTable + ", selection=" + getSelection()
                + ", selectionArgs=" + Arrays.toString(getSelectionArgs()) + "]";
    }

    /**
     * Execute query using the current internal state as {@code WHERE} clause.
     */
    public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
        return query(db, columns, null, orderBy);
    }

    /**
     * Execute query using the current internal state as {@code WHERE} clause.
     */
    private Cursor query(SQLiteDatabase db, String[] columns, String groupBy,
                         String orderBy) {
        assertTable();
        if (columns != null) mapColumns(columns);
        return db.query(mTable, columns, getSelection(), getSelectionArgs(), groupBy, null,
                orderBy, null);
    }

    /**
     * Execute update using the current internal state as {@code WHERE} clause.
     */
    public int update(SQLiteDatabase db, ContentValues values) {
        assertTable();
        return db.update(mTable, values, getSelection(), getSelectionArgs());
    }

    /**
     * Execute delete using the current internal state as {@code WHERE} clause.
     */
    public int delete(SQLiteDatabase db) {
        assertTable();
        return db.delete(mTable, getSelection(), getSelectionArgs());
    }
}
