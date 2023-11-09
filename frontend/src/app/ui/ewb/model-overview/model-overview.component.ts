import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Topic } from '@app/core/model/ewb/topic.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';
import { TopicViewComponent } from './topic-view/topic-view.component';
import { MatRadioChange } from '@angular/material/radio';
import { CustomSeriesRenderItemReturn, EChartsOption } from 'echarts';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { MatButtonToggleChange } from '@angular/material/button-toggle';
import * as d3 from 'd3';
import { Theta } from '@app/core/model/ewb/theta.model';
import { TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { DocumentViewComponent } from '../modules/document-view/document-view.component';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-model-overview',
  templateUrl: './model-overview.component.html',
  styleUrls: ['./model-overview.component.scss'],
  animations: GENERAL_ANIMATIONS
})
export class ModelOverviewComponent extends BaseComponent implements OnInit {

	@Input() model: string;
	@Input() corpus: string;
	topics: TopicMetadata[] = [];
	selectedView: string = '1';
	chartOptions: any = null;
	useRelation: string = '1';
	private vocabularies: any;
	private areLoaded: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
	private topicNum = 0;
	private count = 1;

  constructor(private ewbService: EwbService, private dialog: MatDialog) {
	super();
   }

  ngOnInit(): void {
	if (this.model !== null && this.model !== undefined) {
		this.ewbService.getVocabularyForTopics(this.model)
		.pipe(takeUntil(this._destroyed))
		.subscribe(result => {
			this.vocabularies = result;
			this.ewbService.getAllTopicMetadata(this.model)
					.pipe(takeUntil(this._destroyed))
					.subscribe((result: TopicMetadata[]) => {
						this.topics = result;
						switch(this.selectedView) {
							case '1':
								this.makeDefaultViewOptions();
							break;
						}
					});
				});
	}


  }

  onOverViewChange(event: MatButtonToggleChange) {
	this.useRelation = event.value;
	this.chartOptions = null;
	this.makeDefaultViewOptions();
  }

  private makeDefaultViewOptions() {
	let x = 0;
	let y = 0;
	let index =0;
	const nodes = this.topics.map((topic: TopicMetadata) => {
		const size = 60 * (topic.topic_entropy * 2);
		const data = {
			id: topic.id,
			name: topic.tpc_labels,
			value: 0,
			x: x,
			y: y,
			symbolSize: size,
			label: {
				show: true,
				formatter: this.getTopWords(topic.id)
			},
			itemStyle: {
				color: 'aliceblue'
			},
			emphasis: {
				itemStyle: {
					color: 'lightblue',
					overflow: 'break'
				},
				label: {
					formatter: '{b}',
					overflow: 'break'
				}
			}
		};
		if (index === 9) {
			index = 0;
			x = 0;
			y = y + (this.useRelation === '1' ? 0 : size);
		} else {
			x = x + (this.useRelation === '1' ? 0 : size);
		}
		index = index + 1;
		return data;
	});
	const links = [];
	if (this.useRelation === '1') {
		this.ewbService.getTopicRelations(this.model)
		.pipe(takeUntil(this._destroyed)).subscribe((relations) => {
			nodes.forEach(node => {
				const relation = relations.filter(r => r.id === node.id)[0];
				relation.correlations.filter(c => c.id !== relation.id).forEach((value) => {
					const relatedNode = nodes.filter(x => x.id === value.id)[0];
					// const linkIndex = links.findIndex(link => link.id === relatedNode.id + '-' + node.id);
					// if (linkIndex > -1) {
					// 	links[linkIndex].value += value.score;
					// } else {
						links.push({
							id: node.id + '-' + relatedNode.id,
							source: node.id,
							target: relatedNode.id,
							value: value.score,
							lineStyle: {
								width: 0
							}
						});
					//}
					// const offset = value.score * 1.5;
					// const xdelta = Math.abs(relatedNode.x - node.x);
					// const ydelta = Math.abs(relatedNode.y - node.y);
					// const xoffset = (relatedNode.x === node.x) ? 0 : (relatedNode.x > node.x) ? Math.min(offset, xdelta) : (Math.min(offset, xdelta)) * -1;
					// const yoffset = (relatedNode.y === node.y) ? 0 : (relatedNode.y > node.y) ? Math.min(offset, ydelta) : (Math.min(offset, ydelta)) * -1;
					// node.x = node.x + xoffset;
					// node.y = node.y + yoffset;
				});
			});
			this.chartOptions = {
				series: {
					type: 'graph',
					layout: 'force',
					roam: true,
					autoCurveness: true,
					nodes: nodes,
					links: links,
					nodeScaleRatio: 0, //GK: Why 0.6 is a TYPE???
					force: {
						repulsion: [0, 1],
						edgeLength: [0, 1],
						layoutAnimation: false
					}
				} as any
			};
			console.log(JSON.stringify(this.chartOptions));
		});
	} else {
		this.chartOptions = {
			series: {
				type: 'graph',
				layout: 'none',
				nodes: nodes
			}
		};
		console.log(JSON.stringify(this.chartOptions));
	}


  }

  private makeTemporalViewOptions() {
	this.ewbService.getTopicsTemporal(this.corpus, this.model)
	.pipe(takeUntil(this._destroyed))
	.subscribe( response => {
		let years: any[] = [];
		const values: Map<string, number[]> = new Map();
		const topics = this.topics.map(topic => topic.tpc_labels);
		Object.entries(response).forEach(value => {
			values.set(value[0], []);
			Object.entries(value[1]).forEach(val => {
				if (!years.includes(val[0])) {
					years.push(val[0] + '');
				}
				values.get(value[0]).push(val[1]);
			});
		});
		console.log(JSON.stringify(years));
		this.chartOptions = {
			title: {
				text: ''
			},
			legend: {
				orient: 'vertical',
				right: 10,
				top: 'center',
				backgroundColor: '#fff',
				textStyle: {
					width: 120,
					overflow: 'truncate'
				},
				type: 'scroll'
			},
			tooltip: {
				trigger: 'axis',
				confine: true
			},
			dataZoom: {
				type: 'inside'
			},
			xAxis: [
				{
					type: 'category',
					data: years,
					boundaryGap: true,
        			silent: false,
        			splitLine: {
            			show: false
        			},
        			axisLabel: {}
				}
			],
			yAxis: {
			},
			series: this.topics.map((topic, index) => {
				return {
					name: topic.tpc_labels,
					id: topic.id,
					type: 'line',
					stack: 'x',
					areaStyle: {},
					emphasis: {
						focus: 'series'
					},
					data: values.get(topic.id)
				}
			}),
			toolbox: {
				show: true,
				feature: {
					saveAsImage: {
						type: 'png'
					}
				}
			}
		};
		console.log(JSON.stringify(this.chartOptions));
	});
  }

  private makeHierarchicalViewOptions() {
	this.ewbService.getTopicsHierarchical(this.corpus, this.model)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		const seriesData = [];
		seriesData.push({
			id: 'root',
			value: '',
			depth: 0,
			index: seriesData.length
		})
		Object.entries(result).forEach(val => {
			seriesData.push({
				id: `${val[0]}`,
				value: this.getTopicRelevance(val[0]),
				depth: 1,
				index: seriesData.length
			});
			(val[1] as TopDoc[]).forEach(doc => {
				seriesData.push({
					id: `${val[0]}.${doc.id}`,
					value: doc.words,
					depth: 2,
					index: seriesData.length
				});
			});
			const displayRoot = stratify();
  function stratify() {
    return d3
      .stratify()
      .parentId((d: any) => {
		if (d.id == 'root') {
			return '';
		} else if (d.id.lastIndexOf('.') < 0) {
			return 'root';
		} else {
        	return d.id.substring(0, d.id.lastIndexOf('.'));
		}
      })(seriesData)
      .sum((d: any) => {
        return d.value || 0;
      })
      .sort((a, b) => {
        return b.value - a.value;
      });
  }
  function overallLayout(params, api) {
    var context = params.context;
    d3
      .pack()
      .size([api.getWidth() - 2, api.getHeight() - 2])
      .padding(3)(displayRoot);
    context.nodes = {};
    displayRoot.descendants().forEach(function (node, index) {
      context.nodes[node.id] = node;
    });
  }
  function renderItem(params, api): CustomSeriesRenderItemReturn {
    var context = params.context;
    // Only do that layout once in each time `setOption` called.
    if (!context.layout) {
      context.layout = true;
      overallLayout(params, api);
    }
    var nodePath = api.value('id');
    var node = context.nodes[nodePath];
    if (!node) {
      // Reder nothing.
      return;
    }
    var isLeaf = !node.children || !node.children.length;
    var focus = new Uint32Array(
      node.descendants().map(function (node) {
        return node.data.index;
      })
    );
    var nodeName = isLeaf
      ? nodePath
          .slice(nodePath.lastIndexOf('.') + 1)
          .split(/(?=[A-Z][^A-Z])/g)
          .join('\n')
      : '';
    var z2 = api.value('depth') * 2;
    return {
      type: 'circle',
      focus: focus,
      shape: {
        cx: node.x,
        cy: node.y,
        r: node.r
      },
      transition: ['shape'],
      z2: z2,
      textContent: {
        type: 'text',
        style: {
          // transition: isLeaf ? 'fontSize' : null,
          text: nodeName,
          fontFamily: 'Arial',
          width: node.r * 1.3,
          overflow: 'truncate',
          fontSize: node.r / 3
        },
        emphasis: {
          style: {
            overflow: null,
            fontSize: Math.max(node.r / 3, 12)
          }
        }
      },
      textConfig: {
        position: 'inside'
      },
      style: {
        fill: api.visual('color')
      },
      emphasis: {
        style: {
          fontFamily: 'Arial',
          fontSize: 12,
          shadowBlur: 20,
          shadowOffsetX: 3,
          shadowOffsetY: 5,
          shadowColor: 'rgba(0,0,0,0.3)'
        }
      }
    };
  }
  this.chartOptions = {
    dataset: {
      source: seriesData
    },
    tooltip: {},
    visualMap: [
      {
        show: false,
        min: 0,
        max: 2,
        dimension: 2,
        inRange: {
          color: ['#006edd', '#e0ffff']
        }
      }
    ],
    hoverLayerThreshold: Infinity,
    series: {
      type: 'custom',
      renderItem: renderItem,
      coordinateSystem: 'none',
      encode: {
        tooltip: 'value',
        itemName: 'id'
      }
    }
  };

		});
	});
  }

  openDialog(event: any) {
	switch(this.selectedView) {
		case '1': {
			this.dialog.open(TopicViewComponent, {
				width: '85vw',
				height: '80vh',
				panelClass: 'topic-style',
				data: {
					corpus: this.corpus,
					model: this.model,
					topicId: event.data.id,
					topicName: this.getTopicName(event.data.id)
				}
			});
		}
		case '3': {
			if (event.data.depth === 1) {
				this.dialog.open(TopicViewComponent, {
					width: '85vw',
					height: '80vh',
					panelClass: 'topic-style',
					data: {
						corpus: this.corpus,
						model: this.model,
						topicId: event.data.id,
						topicName: this.getTopicName(event.data.id)
					}
				});
			} else if (event.data.depth === 2) {
				this.ewbService.getDocument(this.corpus, (event.data.id as string).split('.')[1]).subscribe((doc: any) => {
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
		}
	}
  }

  onRadioChange(event: MatRadioChange) {
	this.selectedView = event.value;
	switch(this.selectedView) {
		case '1':
			this.makeDefaultViewOptions();
			break;
		case '2':
			this.makeTemporalViewOptions();
			break;
		case '3':
			this.makeHierarchicalViewOptions();
			break;
	}
  }

  private getTopWords(topicId: string): string {
	if (this.vocabularies !== undefined && this.vocabularies !== null) {
		const words: any[] = this.vocabularies[topicId];
		let finalString: string = '';
		for (let i = 0; i < 5; i++) {
			finalString = `${finalString}\n${words[i]}`;
		}
		return finalString;
	}
	return 'Test\nteSt';
  }

  private getTopicName(topicId: string): string {
	return this.topics.filter((topic: TopicMetadata) => topic.id === topicId)[0].tpc_labels;
  }

  private getTopicRelevance(topicId: string): number {
	return this.topics.filter((topic: TopicMetadata) => topic.id === topicId)[0].topic_entropy;
  }

}
