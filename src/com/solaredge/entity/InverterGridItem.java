package com.solaredge.entity;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "table_inverter_grid_item")
public class InverterGridItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private int mId;

	@Column(column = "mInverterId")
	private String mInverterId;

	@Column(column = "mRow")
	private int mRow;

	@Column(column = "mCol")
	private int mCol;

	@Column(column = "mAngle")
	private int mAngle;

	@Column(column = "mIsNew")
	private int mIsNew; // 2: scanned grid, 1: added extra optimizer, 0: deleted
						// optimizer

	@Column(column = "mUniversalRow")
	private int mUniversalRow;

	@Column(column = "mUniversalCol")
	private int mUniversalCol;

	@Column(column = "mMacId")
	private String mMacId;

	@Column(column = "mInverterName")
	private String mInverterName;

	public String getInverterId() {
		return mInverterId;
	}

	public void setInverterId(String mInverterId) {
		this.mInverterId = mInverterId;
	}

	public int getRow() {
		return mRow;
	}

	public void setRow(int mRow) {
		this.mRow = mRow;
	}

	public int getCol() {
		return mCol;
	}

	public void setCol(int mCol) {
		this.mCol = mCol;
	}

	public int getAngle() {
		return mAngle;
	}

	public void setAngle(int mAngle) {
		this.mAngle = mAngle;
	}

	public int isIsNew() {
		return mIsNew;
	}

	public void setIsNew(int mIsNew) {
		this.mIsNew = mIsNew;
	}

	public String getInverterName() {
		return mInverterName;
	}

	public void setInverterName(String mInverterName) {
		this.mInverterName = mInverterName;
	}

	public int getUniversalRow() {
		return mUniversalRow;
	}

	public void setUniversalRow(int mUniversalRow) {
		this.mUniversalRow = mUniversalRow;
	}

	public int getUniversalCol() {
		return mUniversalCol;
	}

	public void setUniversalCol(int mUniversalCol) {
		this.mUniversalCol = mUniversalCol;
	}

	public String getMacId() {
		return mMacId;
	}

	public void setMacId(String mMacId) {
		this.mMacId = mMacId;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof InverterGridItem) {
			InverterGridItem grid = (InverterGridItem) object;
			if (mInverterId.equals(grid.getInverterId())
					&& mUniversalRow == grid.getUniversalRow()
					&& mUniversalCol == grid.getUniversalCol()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	@Override
	public String toString() {
		return "<scouter><inverterid>" + mInverterId + "</inverterid><group>"
				+ (mRow + 1) + "</group><locationid>" + (mCol + 1)
				+ "</locationid><macid>" + mMacId + "</macid><tilt>" + mAngle
				+ "</tilt><xaxis>" + (mUniversalCol + 1) + "</xaxis><yaxis>"
				+ (mUniversalRow + 1) + "</yaxis></scouter>";
	}

}
