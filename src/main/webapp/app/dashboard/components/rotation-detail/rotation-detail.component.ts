import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpRotationService} from "../../../shared/services/backend/http-rotation/http-rotation.service";
import {Rotation} from "../../../shared/models/backend/rotation/rotation";
import {HeaderConfiguration} from "../../../shared/models/frontend/header/header-configuration";
import {IconEnum} from "../../../shared/enums/icon.enum";
import {HttpAssetService} from "../../../shared/services/backend/http-asset/http-asset.service";
import {MaterialIconRecords} from "../../../shared/records/material-icon.record";

@Component({
  selector: 'suricate-rotation-detail',
  templateUrl: './rotation-detail.component.html',
  styleUrls: ['./rotation-detail.component.scss']
})
export class RotationDetailComponent implements OnInit {
  /**
   * Hold the configuration of the header component
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * The rotation to display
   */
  public rotation: Rotation;

  /**
   * Used to know if the rotation is loading
   */
  public isRotationLoading = true;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * Constructor
   */
  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly httpRotationService: HttpRotationService
  ) { }

  /**
   * Init method
   */
  ngOnInit(): void {
    this.httpRotationService.getById(this.activatedRoute.snapshot.params['rotationId'])
      .subscribe((rotation: Rotation) => {
        this.isRotationLoading = false;
        this.rotation = rotation;
        this.initHeaderConfiguration();

        console.warn(this.rotation);
      },
      () => {
        this.isRotationLoading = false;
        this.router.navigate(['/home/dashboards']);
        }
      );
  }

  /**
   * Init the header of the rotation detail screen
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: this.rotation.name,
      actions: [
        {
          icon: IconEnum.EDIT,
          color: 'primary',
          variant: 'miniFab',
          tooltip: { message: 'rotation.edit' }
        },
        {
          icon: IconEnum.DELETE,
          color: 'warn',
          variant: 'miniFab',
          tooltip: { message: 'rotation.delete' }
        }
      ]
    };
  }

  /**
   * Get the asset url
   *
   * @param assetToken The asset used to build the url
   */
  public getContentUrl(assetToken: string): string {
    return HttpAssetService.getContentUrl(assetToken);
  }
}
