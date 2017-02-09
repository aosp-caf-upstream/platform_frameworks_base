/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.view.autofill;

import static android.view.autofill.Helper.DEBUG;

import android.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Abstracts how a {@link View} can be auto-filled by an
 * {@link android.service.autofill.AutoFillService}.
 *
 * <p>Each {@link AutoFillValue} has a {@code type} and optionally a {@code sub-type}: the
 * {@code type} defines the view's UI control category (like a text field), while the optional
 * {@code sub-type} define its semantics (like a postal address).
 */
public final class AutoFillValue implements Parcelable {

    private final String mText;
    private final int mListIndex;
    private final boolean mToggle;

    private AutoFillValue(CharSequence text, int listIndex, boolean toggle) {
        mText = (text == null) ? null : text.toString();
        mListIndex = listIndex;
        mToggle = toggle;
    }

    /**
     * Gets the value to auto-fill a text field.
     *
     * <p>See {@link AutoFillType#isText()} for more info.
     */
    public CharSequence getTextValue() {
        return mText;
    }

    /**
     * Gets the value to auto-fill a toggable field.
     *
     * <p>See {@link AutoFillType#isToggle()} for more info.
     */
    public boolean getToggleValue() {
        return mToggle;
    }

    /**
     * Gets the value to auto-fill a selection list field.
     *
     * <p>See {@link AutoFillType#isList()} for more info.
     */
    public int getListValue() {
        return mListIndex;
    }

    /////////////////////////////////////
    //  Object "contract" methods. //
    /////////////////////////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mListIndex;
        result = prime * result + ((mText == null) ? 0 : mText.hashCode());
        result = prime * result + (mToggle ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final AutoFillValue other = (AutoFillValue) obj;
        if (mListIndex != other.mListIndex) return false;
        if (mText == null) {
            if (other.mText != null) return false;
        } else {
            if (!mText.equals(other.mText)) return false;
        }
        if (mToggle != other.mToggle) return false;
        return true;
    }

    @Override
    public String toString() {
        if (!DEBUG) return super.toString();

        if (mText != null) {
            return mText.length() + "_chars";
        }

        return "[listIndex=" + mListIndex + ", toggle=" + mToggle + "]";
    }

    /////////////////////////////////////
    //  Parcelable "contract" methods. //
    /////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mText);
        parcel.writeInt(mListIndex);
        parcel.writeInt(mToggle ? 1 : 0);
    }

    private AutoFillValue(Parcel parcel) {
        mText = parcel.readString();
        mListIndex = parcel.readInt();
        mToggle = parcel.readInt() == 1;
    }

    public static final Parcelable.Creator<AutoFillValue> CREATOR =
            new Parcelable.Creator<AutoFillValue>() {
        @Override
        public AutoFillValue createFromParcel(Parcel source) {
            return new AutoFillValue(source);
        }

        @Override
        public AutoFillValue[] newArray(int size) {
            return new AutoFillValue[size];
        }
    };

    ////////////////////
    // Factory methods //
    ////////////////////

    // TODO(b/33197203): add unit tests for each supported type (new / get should return same value)
    /**
     * Creates a new {@link AutoFillValue} to auto-fill a text field.
     *
     * <p>See {@link AutoFillType#isText()} for more info.
     */
    // TODO(b/33197203): use cache
    @Nullable
    public static AutoFillValue forText(@Nullable CharSequence value) {
        return value == null ? null : new AutoFillValue(value, 0, false);
    }

    /**
     * Creates a new {@link AutoFillValue} to auto-fill a toggable field.
     *
     * <p>See {@link AutoFillType#isToggle()} for more info.
     */
    public static AutoFillValue forToggle(boolean value) {
        return new AutoFillValue(null, 0, value);
    }

    /**
     * Creates a new {@link AutoFillValue} to auto-fill a selection list field.
     *
     * <p>See {@link AutoFillType#isList()} for more info.
     */
    public static AutoFillValue forList(int value) {
        return new AutoFillValue(null, value, false);
    }
}
