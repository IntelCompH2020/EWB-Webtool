import { ViewEncapsulation } from '@angular/compiler';
import { Component, Inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PercentValuePipe } from '@app/core/formatting/pipes/percentage.pipe';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { TopicBeta, TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { TopDocTopicQuery } from '@app/core/query/top-doc-topic.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

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

  constructor(
	private dialogRef: MatDialogRef<TopicViewComponent>,
	private ewbService: EwbService,
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
		console.log(JSON.stringify(result));
		this.setupTopDocColumns();
	});

	this.ewbService.getTopicTopWords(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.words = result;
		this.maxValue = this.words.reduce((prev, curr) => (prev.beta > curr.beta)? prev : curr).beta;
		console.log(JSON.stringify(result));
		this.setupTopWordColumns();
	});
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
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 250,
			minWidth: 200,
			languageName: '%',
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

}
