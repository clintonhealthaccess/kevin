package org.chai.kevin.survey;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity(name = "SurveyTableQuestion")
@Table(name = "dhsst_survey_table_question")
public class SurveyTableQuestion extends SurveyQuestion {

	private List<SurveyTableColumn> columns;
	private List<SurveyTableRow> rows;

	public void setColumns(List<SurveyTableColumn> columns) {
		this.columns = columns;
	}

	@OneToMany(targetEntity = SurveyTableColumn.class, mappedBy = "question")
	public List<SurveyTableColumn> getColumns() {
		return columns;
	}

	public void setRows(List<SurveyTableRow> rows) {
		this.rows = rows;
	}

	@OneToMany(targetEntity = SurveyTableRow.class, mappedBy = "question")
	public List<SurveyTableRow> getRows() {
		return rows;
	}

	@Transient
	@Override
	public String getTemplate() {
		String gspName = "tableQuestion";
		return gspName;
	}

	@Transient
	public void addColumn(SurveyTableColumn column) {
		column.setQuestion(this);
		columns.add(column);
	}

	@Transient
	public void addRow(SurveyTableRow row) {
		row.setQuestion(this);
		rows.add(row);
	}

}
