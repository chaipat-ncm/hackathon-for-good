<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Conversations
</#macro>

<#macro pageContent>

<h1 class="page-title"> Results</h1>
<div class="row">
<div class="col-md-12">
    <!-- BEGIN MARKERS PORTLET-->
    <div class="portlet light portlet-fit bordered">
        <div class="portlet-title">
            <div class="caption">
                <i class="fa fa-table font-grey-gallery"></i>
                <span class="caption-subject font-grey-gallery bold uppercase">Data</span>
            </div>
        </div>
        <div class="portlet-body">
            <table id="datatable" class="table table-striped table-bordered" width="100%"></table>
        </div>
    </div>
    <!-- END MARKERS PORTLET-->

    <p>Delete all results from the database</p>
    <p><button id="delData" type="button" class="btn btn-danger" data-toggle="confirmation" data-singleton="true"> <i class="fa fa-trash"></i> Delete results from DB</button></p>
</div>
</div>


</#macro>

<#macro pageScript>

<script>
    $(document).ready(() => {
      function getConversations() {
          $.get('/api/v1/db/result', function (rs) {

              const table = $.c4i.datatable('#datatable', [
                  {title: "conversation", data: "conversationId", defaultContent:"-"},
                  {title: "label", data: "label", defaultContent:"-"},
                  {title: "value", data: "value", defaultContent:"-"},
                  {title: "props", data: "props", defaultContent:"-"},
              ], rs, [[0, 'asc']],
                      [
                          {
                              "render": function ( data, type, row ) {
                                  return JSON.stringify(data)
                              },
                              "targets": [3]
                          },
                      ])
          })
      }

      getConversations()


        $("#delData").on("confirmed.bs.confirmation", function() {
            $.ajax({
                url: '/api/v1/db/result',
                type: 'DELETE',
                success: function(result) {
                    $.bootstrapGrowl(
                            'Successful reset.',
                            {
                                ele: 'body', // which element to append to
                                type: 'success', // (null, 'info', 'danger', 'success')
                                offset: {from: 'top', amount: 120}, // 'top', or 'bottom'
                                align: 'right', // ('left', 'right', or 'center')
                                width: 250, // (integer, or 'auto')
                                delay: 5000, // Time while the message will be displayed. It's not equivalent to the *demo* timeOut!
                                allow_dismiss: true, // If true then will display a cross to close the popup.
                                stackup_spacing: 10 // spacing between consecutively stacked growls.
                            });
                    getConversations()
                }
            });
        });

    })
</script>

</#macro>