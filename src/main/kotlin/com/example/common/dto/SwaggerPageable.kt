
package com.example.common.dto

import io.swagger.annotations.ApiModelProperty

data class SwaggerPageable(
    @ApiModelProperty("Page number (1..N). Default: 1", example = "1")
    val page: Int = 1,
    @ApiModelProperty("Number of items per page. Default: 30", example = "30")
    val item: Int = 30
)
