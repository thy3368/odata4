export interface Component {
  id: string;
  type: string;
  props: Record<string, any>;
  children?: Component[];
}

export interface Page {
  id: string;
  name: string;
  components: Component[];
  layout: any;
}

export interface Application {
  id: string;
  name: string;
  pages: Page[];
  dataModels: DataModel[];
  workflows: Workflow[];
}

export interface DataModel {
  id: string;
  name: string;
  fields: Field[];
  relations: Relation[];
}

export interface Field {
  id: string;
  name: string;
  type: FieldType;
  required: boolean;
  defaultValue?: any;
  validations?: ValidationRule[];
}

export interface Relation {
  id: string;
  name: string;
  type: RelationType;
  sourceModel: string;
  targetModel: string;
  sourceField: string;
  targetField: string;
}

export interface WorkflowNode {
  id: string;
  type: WorkflowNodeType;
  position: Position;
  data: Record<string, any>;
}

export interface WorkflowEdge {
  id: string;
  source: string;
  target: string;
  condition?: Record<string, any>;
}

export interface Position {
  x: number;
  y: number;
}

export enum FieldType {
  String = 'string',
  Number = 'number',
  Boolean = 'boolean',
  Date = 'date',
  Object = 'object',
  Array = 'array',
}

export enum RelationType {
  OneToOne = 'oneToOne',
  OneToMany = 'oneToMany',
  ManyToOne = 'manyToOne',
  ManyToMany = 'manyToMany',
}

export enum WorkflowNodeType {
  Start = 'start',
  End = 'end',
  Task = 'task',
  Decision = 'decision',
  Fork = 'fork',
  Join = 'join',
}

export interface ValidationRule {
  type: string;
  params?: Record<string, any>;
  message: string;
}

export interface Workflow {
  id: string;
  name: string;
  nodes: WorkflowNode[];
  edges: WorkflowEdge[];
}