import { Component, Inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PercentValuePipe } from '@app/core/formatting/pipes/percentage.pipe';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { TopicBeta, TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { TopDocTopicQuery } from '@app/core/query/top-doc-topic.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { DocumentViewComponent } from '../../modules/document-view/document-view.component';
import { Dialog } from '@angular/cdk/dialog';
import { SelectionType } from '@swimlane/ngx-datatable';

@Component({
  selector: 'app-topic-view',
  templateUrl: './topic-view.component.html',
  styleUrls: ['./topic-view.component.scss']
})
export class TopicViewComponent extends BaseComponent implements OnInit {

	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;
	@ViewChild('percentageBar', { static: true }) percentageBar: TemplateRef<any>;

	maxValue = 100;

	documents: TopDoc[] = [];
	topDocColumns: ColumnDefinition[] = [];
	topicMetadata: TopicMetadata = null;
	topicName: string = '';
	words: TopicBeta[] = [];
	topWordColumns: ColumnDefinition[] = [];
	isRelevant: boolean = false;

	public selectionType = SelectionType;

  constructor(
	private dialogRef: MatDialogRef<TopicViewComponent>,
	private ewbService: EwbService,
	private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) private data: {
		corpus: string,
		model: string,
		topicId: string,
		topicName: string
	}) {
		super();
	 }

  ngOnInit(): void {
	this.topicName = this.data.topicName;

	this.ewbService.getTopicMetadata(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe((result) => {
		this.topicMetadata = result;
	});

	const topDocTopicQuery: TopDocTopicQuery = {
		corpusCollection: this.data.corpus,
		modelName: this.data.model,
		topicId: +this.data.topicId.charAt(1),
		start: 0,
		rows: 10
	};

	this.ewbService.getTopDocs(topDocTopicQuery)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.documents = result;
		this.maxValue = this.documents.reduce((prev, curr) => (prev.topic > curr.topic)? prev : curr).topic;
		this.setupTopDocColumns();
	});

	this.ewbService.getTopicTopWords(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.words = result;
		this.maxValue = this.words.reduce((prev, curr) => (prev.beta > curr.beta)? prev : curr).beta;
		this.setupTopWordColumns();
	});

	this.ewbService.isTopicRelative(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => this.isRelevant = result);
  }

  private setupTopDocColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.topDocColumns.push(...[
		{
			prop: nameof<TopDoc>(x => x.title),
			name: nameof<TopDoc>(x => x.title),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'Title',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<TopDoc>(x => x.words),
			name: nameof<TopDoc>(x => x.words),
			sortable: true,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 250,
			minWidth: 200,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.RELEVANCE',
			headerClass: 'pretty-header',
			cellTemplate: this.percentageBar,
			pipe: pipe
		},
		{
			prop: nameof<TopDoc>(x => x.topic),
			name: nameof<TopDoc>(x => x.topic),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 150,
			minWidth: 100,
			languageName: 'Tokens',
			headerClass: 'pretty-header'
		}
	]);
  }

  private setupTopWordColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.topWordColumns.push(...[
		{
			prop: nameof<TopicBeta>(x => x.id),
			name: 'Word',
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'Word',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<TopicBeta>(x => x.beta),
			name: nameof<TopicBeta>(x => x.beta),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			languageName: 'Weight',
			cellTemplate: this.percentageBar,
			headerClass: 'pretty-header',
			pipe: pipe
		}
	])
  }

  close() {
	this.dialogRef.close();
  }

  emptyValue(value: number): number {
	return 100 - value;
  }

  showDocument(event: any) {
	this.ewbService.getDocument(this.data.corpus, event[0].id).subscribe((doc: any) => {
		this.dialog.open(DocumentViewComponent, {
			width: '85vw',
			height: '80vh',
			panelClass: 'topic-style',
			data: {
				selectedDoc: doc
			}
		});
	});
  }

  sortRows(ev: any) {
	const column = ev.sortDescriptors[0].property;
	if (column === nameof<TopDoc>(x => x.words)) {
		this.ewbService.getNumOfDocs(this.data.corpus).subscribe(num => {
			const topDocTopicQuery: TopDocTopicQuery = {
				corpusCollection: this.data.corpus,
				modelName: this.data.model,
				topicId: +this.data.topicId.charAt(1),
				start: ev.newValue === 'asc' ? num - 10 : 0,
				rows: 10
			};
			this.ewbService.getTopDocs(topDocTopicQuery).subscribe(result => {
				this.documents = result;
			});
		});
	}

  }

  addRelevant() {
	this.ewbService.addRelevantTopic(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => this.isRelevant = true);
  }

  removeRelevant() {
	this.ewbService.removeRelevantTopic(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(() => this.isRelevant = false);

  }

}
