import { Component, OnInit } from '@angular/core';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-ewb',
  templateUrl: './ewb.component.html',
  styleUrls: ['./ewb.component.scss']
})
export class EwbComponent implements OnInit {
  selectedModel: string = null;
  selectedCorpus: string = null;

  constructor() {
   }

  ngOnInit(): void {
  }

  registerValues(ev: {corpus: string, model: string}) {
	this.selectedCorpus = ev.corpus;
	this.selectedModel = ev.model;
  }

}
