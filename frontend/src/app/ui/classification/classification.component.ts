import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Classification } from '@app/core/model/ewb/classification.model';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { ColumnDefinition } from '@common/modules/listing/listing.component';
import { pipe } from 'rxjs';
import { nameof } from 'ts-simple-nameof';

@Component({
	selector: 'app-classification',
	templateUrl: './classification.component.html',
	styleUrls: ['./classification.component.scss']
})
export class ClassificationComponent implements OnInit {

	selectedCorpus: string = null;
	selectedModel: string = null;
	columns: ColumnDefinition[] = [];
	text: string = null;
	data: Classification[] = [];
	dataCount: number = 0;

	constructor() { }

	ngOnInit(): void {
		this.setupColumns();
		this.setDummyData();
	}

	onTextInputed(ev: string) {
		this.text = ev;
	}

	registerValues(ev: { corpus: string, model: string }) {
		this.selectedCorpus = ev.corpus;
		this.selectedModel = ev.model;
	}

	private setupColumns() {
		this.columns.push(...[
			{
				prop: nameof<Classification>(x => x.type),
				name: nameof<Classification>(x => x.type),
				sortable: false,
				resizeable: false,
				alwaysShown: true,
				canAutoResize: true,
				minWidth: 250,
				languageName: 'Code'
			},
			{
				prop: nameof<Classification>(x => x.value),
				name: nameof<Classification>(x => x.value),
				sortable: false,
				resizeable: false,
				alwaysShown: true,
				isTreeColumn: false,
				canAutoResize: true,
				minWidth: 125,
				languageName: 'Punctuation'
			}
		]);
	}

	private setDummyData() {
		this.dataCount = 3;
		this.data.push(...[
			{
				type: 'banana',
				value: 9001
			},
			{
				type: 'avocado',
				value: 70457
			},
			{
				type: 'Test',
				value: 7357
			}
		]);
	}

}
