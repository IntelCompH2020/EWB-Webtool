import { NgModule } from '@angular/core';
import { EwbComponent } from './ewb.component';
import { EwbRoutingModule } from './ewb-routing.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { ModelOverviewComponent } from './model-overview/model-overview.component';
import { TopicViewComponent } from './model-overview/topic-view/topic-view.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { NgxEchartsModule } from 'ngx-echarts';
import { CdkScrollableModule } from '@angular/cdk/scrolling';
import { DirectiveModule } from '@common/directive/directive.module';
import { SimilaritiesComponent } from './similarities/similarities.component';
import { ListingModule } from '@common/modules/listing/listing.module';
import { DocumentViewComponent } from './modules/document-view/document-view.component';
import { DocumentSearchComponent } from './modules/document-search/document-search.component';
import { DocumentSearchViewComponent } from './document-search-view/document-search-view.component';
import { CorpusModelModule } from '@common/modules/corpus-model/corpus-model.module';
import { TopicRelevanceComponent } from './model-overview/topic-relevance/topic-relevance.component';

@NgModule({
  declarations: [
    EwbComponent,
    ModelOverviewComponent,
    TopicViewComponent,
    DocumentSearchComponent,
    DocumentSearchViewComponent,
    SimilaritiesComponent,
    DocumentViewComponent,
    TopicRelevanceComponent
  ],
  imports: [
    CommonUiModule,
    CommonFormsModule,
    EwbRoutingModule,
    MatGridListModule,
    CdkScrollableModule,
    ListingModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),
    DirectiveModule,
    CorpusModelModule
  ]
})
export class EwbModule { }
